package com.ichbinluka.model

import GameMessage
import GameMessageData
import PlayerData
import getBulletDamage
import io.ktor.websocket.*
import sessions.SessionCreationInfo
import sessions.SessionInfo
import sessions.Topic
import util.Scrollable
import util.ScrollableList


class Session(
    val creationInfo: SessionCreationInfo,
    val id: Long
) {
    val players = mutableListOf<Player>()

    var host: PlayerData = creationInfo.host

    val hasStarted: Boolean get() = currentTurn != null

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

        for (player in players) {
            player.initPlayer(startData.state.tanks.first { it.player == player.data })
        }
        turnIterator = ScrollableList(players)
        turnIterator?.nextWhile { it.data.name != startData.state.currentTurn }
    }

    /**
     * @return true if there are more turns to be played, false if the game is over
     */
    fun nextTurn(): GameMessageData.GameEnd? {
        val alivePlayers = players.filter { it.health > 0 }
        if (alivePlayers.size <= 1) {
            for (player in alivePlayers) {
                deadPlayers.addFirst(player.data)
            }
            turnIterator = null
            return GameMessageData.GameEnd(
                first = deadPlayers.first().name,
                second = deadPlayers.getOrNull(1)?.name ?: "",
                third = deadPlayers.getOrNull(2)?.name ?: ""
            )
        }
        turnIterator?.next()
        turnIterator?.nextWhile { it.health <= 0 }
        return null
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
                iter.remove()
                playersChanged = true
            }
        }
        if (playersChanged) {
            broadcastUpdate()
        }
    }

    fun handleBulletImpact(data: GameMessageData.BulletImpact) {
        for (player in players) {
            player.health -= getBulletDamage(bullet = data.type, distance = data.pos.distanceTo(player.position))
            if (player.health <= 0) {
                deadPlayers.addFirst(player.data)
            }
            println("Player pos: ${player.position}, bullet pos: ${data.pos}")
            println("Player ${player.data.name} health: ${player.health}")
        }
    }

    suspend fun broadcastUpdate() {
        broadcast(
            GameMessage.fromGameMessageData<GameMessageData.SessionStateUpdate>(
                GameMessageData.SessionStateUpdate(toInfo()),
                Topic.SESSION_STATE
            ),
            null
        )
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
        currentTurn = currentTurn!!.data.name
    )

    fun isEmpty(): Boolean = players.isEmpty()
    fun playerConfigAvailable(playerData: PlayerData): Boolean {
        return players.none {
            it.data.name == playerData.name ||
                    it.data.color.toVec3().distanceTo(playerData.color.toVec3()) < 0.1
        }
    }
}