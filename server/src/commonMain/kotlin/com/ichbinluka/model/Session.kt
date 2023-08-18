package com.ichbinluka.model

import GameMessage
import GameMessageData
import PlayerData
import getBulletDamage
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
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

    val currentTurn: Player? get() = turnIterator?.current

    private var turnIterator: Scrollable<Player>? = null

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
    fun nextTurn(): Boolean {
        if (players.filter { it.health > 0 }.size <= 1) {
            turnIterator = null
            return false
        }
        turnIterator?.next()
        turnIterator?.nextWhile { it.health <= 0 }
        return true
    }

    suspend fun broadcast(message: GameMessage, player: Player? = null) {
        val iter = players.iterator()
        var playersChanged = false
        val messageBytes = message.toByteArray()
        for (p in iter) {
            if (p.id == player?.id) continue // Skip broadcaster
            try {
                p.session.send(messageBytes)
            } catch (e: ClosedSendChannelException) {
                iter.remove()
                playersChanged = true
            }
        }
        if (playersChanged) {
            broadcastUpdate()
        }
    }

    suspend fun handleBulletImpact(data: GameMessageData.BulletImpact) {
        for (player in players) {
            player.health -= getBulletDamage(bullet = data.type, distance = data.pos.distanceTo(player.position))
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
}