package com.ichbinluka.model

import GameMessage
import GameMessageData
import PlayerData
import com.ichbinluka.exceptions.NoCurrentTurnException
import getBulletDamage
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sessions.SessionCreationInfo
import sessions.SessionInfo
import sessions.Topic
import util.Scrollable
import util.ScrollableList
import kotlin.concurrent.Volatile

private val log = KtorSimpleLogger("Session")


enum class SessionStatus {
    WAITING_FOR_PLAYERS,
    IN_PROGRESS,
    FINISHED
}

class Session(
    val creationInfo: SessionCreationInfo,
    val id: Long
) {
    val players = mutableListOf<Player>()

    @Volatile
    var host: PlayerData = creationInfo.host

    @Volatile
    var status: SessionStatus = SessionStatus.WAITING_FOR_PLAYERS
        private set

    @Volatile
    var awaitingShot: Boolean = true
        private set

    private val awaitingShotMutex: Mutex = Mutex()

    val currentTurn: Player? get() = turnIterator?.current

    private var turnIterator: Scrollable<Player>? = null

    private val deadPlayers = ArrayDeque<PlayerData>()

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    fun begin(startData: GameMessageData.GameStartData) {
        log.debug("Starting session with $startData")
        for (player in players) {
            player.initPlayer(startData.state.tanks.first { it.player == player.data })
        }
        turnIterator = ScrollableList(players)
        turnIterator?.nextWhile { it.data.name != startData.state.currentTurn }
        status = SessionStatus.IN_PROGRESS
    }

    /**
     * Ends the current turn and starts the next one.
     */
    suspend fun nextTurn() {
        val alivePlayers = players.filter { it.health > 0 }
        if (alivePlayers.size <= 1) {
            for (player in alivePlayers) {
                deadPlayers.addFirst(player.data)
            }
            turnIterator = null
            status = SessionStatus.FINISHED
            log.debug("Game finished")
            broadcast(
                GameMessage.fromGameMessageData(
                    GameMessageData.GameEnd(
                        first = deadPlayers.first().name,
                        second = deadPlayers.getOrNull(1)?.name ?: "",
                        third = deadPlayers.getOrNull(2)?.name ?: ""
                    ),
                    Topic.MAIN_SCENE
                )
            )
            return
        }
        turnIterator?.next()
        turnIterator?.nextWhile { it.health <= 0 }
        awaitingShotMutex.withLock {
            awaitingShot = true
        }
        log.debug("Next player: ${currentTurn?.data?.name}")
        broadcast(
            GameMessage.fromGameMessageData(
                getState(),
                Topic.MAIN_SCENE
            )
        )
    }

    suspend fun broadcast(message: GameMessage, player: Player? = null) {
        val iter = players.iterator()
        var playersChanged = false
        val messageBytes = message.toByteArray()
        for (p in iter) {
            if (p.id == player?.id) continue // Skip broadcaster
            try {
                p.session.send(messageBytes)
            } catch (e: Throwable) {
                log.info("Could not send message to ${p.data.name}: ${e.message}")
                iter.remove()
                playersChanged = true
            }
        }
        if (playersChanged) {
            broadcastUpdate()
        }
    }

    suspend fun handleBulletSpawn(data: GameMessageData.BulletCreationData) {
        awaitingShotMutex.withLock {
            awaitingShot = false
        }
        for (player in players) {
            player.positionBeforeImpact = player.position
        }
        currentTurn?.shoot(data.type)
    }

    fun handleBulletImpact(data: GameMessageData.BulletImpact) {
        for (player in players) {
            if (player.data.name == data.intersectingName) {
                player.health -= getBulletDamage(bullet = data.type, distance = 0.0)
            } else {
                player.health -= getBulletDamage(
                    bullet = data.type,
                    distance = data.pos.distanceTo(player.positionBeforeImpact)
                )
            }
        }
        updateDeadPlayers()
    }

    fun updateDeadPlayers() {
        for (player in players) {
            if (player.health <= 0) {
                deadPlayers.addFirst(player.data)
            }
        }
    }

    suspend fun broadcastUpdate() {
        when (status) {
            SessionStatus.WAITING_FOR_PLAYERS -> {
                broadcast(
                    GameMessage.fromGameMessageData<GameMessageData.SessionInfoUpdate>(
                        GameMessageData.SessionInfoUpdate(toInfo()),
                        Topic.SESSION_STATE
                    ),
                    null
                )
            }

            SessionStatus.IN_PROGRESS -> {
                broadcast(
                    GameMessage.fromGameMessageData(
                        getState(),
                        Topic.MAIN_SCENE
                    )
                )
            }

            else -> {}
        }

    }

    suspend fun handleDisconnect(player: Player) {
        when (status) {
            SessionStatus.WAITING_FOR_PLAYERS -> {
                removePlayer(player)
                broadcastUpdate()
            }

            SessionStatus.IN_PROGRESS -> {
                player.health = 0.0
                updateDeadPlayers()
                if (player == currentTurn) {
                    nextTurn()
                } else {
                    broadcast(
                        GameMessage.fromGameMessageData(
                            getState(),
                            Topic.MAIN_SCENE
                        )
                    )
                }
            }

            SessionStatus.FINISHED -> {
                // Do nothing
            }
        }
    }

    fun toInfo(): SessionInfo = SessionInfo(
        id = id,
        name = creationInfo.name,
        host = host,
        players = players.map { it.data },
        password = creationInfo.password,
    )

    fun getState(): GameMessageData.GameStateData = GameMessageData.GameStateData(
        tanks = players.map { it.toTankData() },
        currentTurn = currentTurn?.data?.name ?: throw NoCurrentTurnException(),
    )

    fun isEmpty(): Boolean = players.isEmpty()
    fun playerConfigAvailable(playerData: PlayerData): Boolean {
        return players.none {
            it.data.name == playerData.name ||
                    it.data.color.toVec3().distanceTo(playerData.color.toVec3()) < 0.1
        }
    }
}