package com.ichbinluka.services

import com.ichbinluka.routes.SessionCreationInfo
import sessions.Session


interface GameSessionService {
    fun getSessionById(sessionId: Long): Session?

    fun getPublicSessions(): Collection<Session>

    fun addSession(session: Session): Long

    fun createSession(info: SessionCreationInfo): Session
    fun closeSession(sessionId: Long)
}