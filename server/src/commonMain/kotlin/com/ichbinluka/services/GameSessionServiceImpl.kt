package com.ichbinluka.services

import GameMessage
import GameMessageData
import com.ichbinluka.model.Session
import sessions.SessionCreationInfo
import sessions.Topic


class GameSessionServiceImpl : GameSessionService {

    private var currentId: Long = 1L
    private val nextId: Long get() = currentId++

    private val sessions = hashMapOf<Long, Session>()

    override fun getSessionById(sessionId: Long): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter {
        it.creationInfo.password == null &&
                !it.hasStarted &&
                it.players.size < 5
    }

    override fun addSession(session: Session): Long {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun createSession(info: SessionCreationInfo): Session {
        val id = nextId
        val session = info.run {
            Session(
                SessionCreationInfo(
                    password = password,
                    name = name,
                    host = host,
                ),
                id
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