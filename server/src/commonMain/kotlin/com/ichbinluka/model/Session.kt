package com.ichbinluka.model

import GameMessage
import GameMessageData
import PlayerData
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import sessions.SessionCreationInfo
import sessions.SessionInfo
import sessions.Topic
import util.ScrollableList


class Session(
    val creationInfo: SessionCreationInfo,
    val id: Long
) {
    val players = mutableListOf<Player>()

    var host: PlayerData = creationInfo.host

    var currentTurn: Player? = null
    private set

    var currentState: GameMessageData.GameStateData? = null
    private set

    private var turnIterator: Iterator<Player>? = null

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun removePlayer(player: Player) {
        players.remove(player)
    }

    fun begin(stateData: GameMessageData.GameStartData) {
        currentState = stateData.state.copy()
        turnIterator = ScrollableList(players)
        currentTurn = players.first()
        updateState()
    }

    fun nextTurn() {
        currentTurn = turnIterator?.next()
        updateState()
    }

    private fun updateState() {
        currentState = currentState?.copy(currentTurn = currentTurn!!.data.name)
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

    fun isEmpty(): Boolean = players.isEmpty()
}