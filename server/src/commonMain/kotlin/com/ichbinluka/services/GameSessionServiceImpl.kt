package com.ichbinluka.services

import ColorData
import Player
import sessions.Session

class GameSessionServiceImpl : GameSessionService {

    private var currentId: Int = 0
    private val nextId: Int get() = currentId++

    private val sessions = hashMapOf<Int, Session>(
        1 to Session(
            host = Player(name = "Bob", color = ColorData(0xffffff)),
            name = "Test"
        )
    )

    override fun getSessionById(sessionId: Int): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter { it.password == null }
    override fun addSession(session: Session): Int {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun openSession(name: String, password: String?, player: Player): Int {
        val id = nextId
        sessions[id] = Session(host = player, name = name, password = password)
        println("opening session")
        return id
    }

    override fun closeSession(sessionId: Int) {
        TODO("Not yet implemented")
    }
}