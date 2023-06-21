package com.ichbinluka.services

import ColorData
import GameMessage
import PlayerData
import com.ichbinluka.model.Session
import io.ktor.websocket.*
import sessions.SessionCreationInfo
import sessions.SessionInfo
import sessions.Topic
import com.ichbinluka.extensions.send


class GameSessionServiceImpl : GameSessionService {

    private var currentId: Long = 1L
    private val nextId: Long get() = currentId++

    private val sessions = hashMapOf<Long, Session>()

    override fun getSessionById(sessionId: Long): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter { it.info.password == null }

    override fun addSession(session: Session): Long {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun createSession(info: SessionCreationInfo): Session {
        val id = nextId
        val session = info.run {
            Session(
                info = SessionInfo(
                    id,
                    password = password,
                    name = name,
                    host = host,
                    players = listOf()
                )
            )
        }
        sessions[id] = session
        return session
    }

    override suspend fun closeSession(sessionId: Long) {
        val session = sessions.remove(sessionId) ?: return
        session.broadcast(GameMessage.fromGameMessageData(
            GameMessageData.Disconnect,
            Topic.GLOBAL
        ))
    }

}