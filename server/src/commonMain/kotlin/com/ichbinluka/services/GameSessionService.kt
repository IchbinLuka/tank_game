package com.ichbinluka.services

import Player
import sessions.Session

interface GameSessionService {
    fun getSessionById(sessionId: Int): Session?

    fun getPublicSessions(): Collection<Session>

    fun addSession(session: Session): Int
    fun openSession(name: String, password: String?, player: Player): Int
    fun closeSession(sessionId: Int)
}