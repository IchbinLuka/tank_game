package com.ichbinluka.services

import com.ichbinluka.model.Session
import sessions.SessionCreationInfo


interface GameSessionService {
    fun getSessionById(sessionId: String): Session?

    fun getPublicSessions(): Collection<Session>

    fun addSession(session: Session): String

    fun createSession(info: SessionCreationInfo, public: Boolean, id: String? = null): Session

    suspend fun closeSession(sessionId: String)
}