package com.ichbinluka.services

import com.ichbinluka.model.Session

class GameSessionServiceImpl : GameSessionService {
    override fun getSessionById(sessionId: Int): Session {
        TODO("Not yet implemented")
    }

    override fun openSession(): Int {
        println("opening session")
        return 1
    }

    override fun closeSession(sessionId: Int) {
        TODO("Not yet implemented")
    }
}