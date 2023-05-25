package com.ichbinluka.services

import ColorData
import Player
import com.ichbinluka.routes.SessionCreationInfo
import io.ktor.util.logging.*
import sessions.Session


class GameSessionServiceImpl : GameSessionService {

    private var currentId: Long = 1L
    private val nextId: Long get() = currentId++

    private val sessions = hashMapOf<Long, Session>(
        0L to Session(
            id = 0,
            host = Player(name = "Bob", color = ColorData(0xffffff)),
            name = "Test"
        )
    )

    override fun getSessionById(sessionId: Long): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter { it.password == null }
    override fun addSession(session: Session): Long {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun createSession(info: SessionCreationInfo): Session {
        val id = nextId
        val session = info.run { Session(id, password = password, name = name, host = host) }
        sessions[id] = session
        return session
    }

    override fun closeSession(sessionId: Long) {
        TODO("Not yet implemented")
    }
}