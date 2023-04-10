package com.ichbinluka.services

import com.ichbinluka.model.Session

interface GameSessionService {
    fun getSessionById(sessionId: Int): Session
    fun openSession(): Int
    fun closeSession(sessionId: Int)
}