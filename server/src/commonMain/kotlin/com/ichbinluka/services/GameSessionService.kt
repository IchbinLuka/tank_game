package com.ichbinluka.services

import GameMessage
import PlayerData
import com.ichbinluka.model.Session
import io.ktor.websocket.*
import sessions.SessionCreationInfo
import sessions.SessionInfo


interface GameSessionService {
    fun getSessionById(sessionId: Long): Session?

    fun getPublicSessions(): Collection<Session>

    fun addSession(session: Session): Long

    fun createSession(info: SessionCreationInfo): Session
    fun closeSession(sessionId: Long)

}