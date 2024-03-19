package com.ichbinluka.services

import GameMessage
import GameMessageData
import com.benasher44.uuid.uuid4
import com.ichbinluka.model.Session
import com.ichbinluka.model.SessionStatus
import io.ktor.util.logging.*
import sessions.SessionCreationInfo
import sessions.Topic

private val log = KtorSimpleLogger("GameSessionService")

class GameSessionServiceImpl : GameSessionService {
    private val nextId: String get() = uuid4().toString()

    private val sessions = hashMapOf<String, Session>()
    private val discordSessions = hashMapOf<String, String>()

    override fun getSessionById(sessionId: String): Session? = sessions[sessionId]

    override fun getPublicSessions(): Collection<Session> = sessions.values.filter {
        it.creationInfo.password == null &&
                it.status == SessionStatus.WAITING_FOR_PLAYERS &&
                it.players.size < 5 &&
                it.public
    }

    override fun addSession(session: Session): String {
        val id = nextId
        sessions[id] = session
        return id
    }

    override fun createSession(info: SessionCreationInfo, public: Boolean, id: String?): Session {
        val sessionId = id ?: nextId
        val session = info.run {
            Session(
                SessionCreationInfo(
                    password = password,
                    name = name,
                    host = host,
                ),
                id = sessionId,
                public = public
            )
        }
        sessions[sessionId] = session
        return session
    }

    override suspend fun closeSession(sessionId: String) {
        val session = sessions.remove(sessionId) ?: return
        session.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.Disconnect,
                Topic.GLOBAL
            )
        )
        log.debug("Session $sessionId closed")
    }

}