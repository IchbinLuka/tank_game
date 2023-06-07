package com.ichbinluka.services

import ColorData
import GameMessage
import PlayerData
import com.ichbinluka.model.Session
import io.ktor.websocket.*
import sessions.SessionCreationInfo
import sessions.SessionInfo


class GameSessionServiceImpl : GameSessionService {

    private var currentId: Long = 1L
    private val nextId: Long get() = currentId++

    private val sessions = hashMapOf<Long, Session>(
        0L to Session(
            info = SessionInfo(id = 0,
                    host = PlayerData(name = "Bob", color = ColorData(0xffffff)),
                    name = "Test")
        )
    )

    override fun getSessionById(sessionId: Long): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter { it.info.password == null }

    override fun addSession(session: Session): Long {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun createSession(info: SessionCreationInfo): Session {
        val id = nextId
        val session = info.run { Session(info = SessionInfo(id, password = password, name = name, host = host)) }
        sessions[id] = session
        return session
    }

    override fun closeSession(sessionId: Long) {
        TODO("Not yet implemented")
    }

}