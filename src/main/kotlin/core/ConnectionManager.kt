package core

import GameMessage
import GameMessageData
import PlayerData
import core.multiplayer.MultiplayerSynchronized
import decodeFromArrayBuffer
import exceptions.ConnectionFailedException
import ext.send
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val WEBSOCKET_URL = "ws://localhost:8081/sessions/connect"

object ConnectionManager {

    private val synchronizedObjects = hashMapOf<String, MutableList<MultiplayerSynchronized>>()
    private var socket: WebSocket? = null

    private val connectMutex = Mutex()

    suspend fun connect(sessionId: Long, playerData: PlayerData) = connectMutex.withLock {
        suspendCoroutine { continuation ->
            socket = WebSocket(WEBSOCKET_URL).apply {
                binaryType = BinaryType.ARRAYBUFFER
                onmessage = ::onMessage
            }
            socket!!.onopen = {
                continuation.resume(Unit)
            }
            socket!!.onerror = {
                continuation.resumeWithException(ConnectionFailedException())
            }

        }
        suspendCoroutine { continuation ->
            socket!!.onopen = {
                continuation.resume(Unit);
            }
            socket!!.onclose = {
                continuation.resumeWithException(ConnectionFailedException())
            }
            socket!!.send(GameMessageData.ConnectionRequest(playerData = playerData, sessionId =  sessionId).toByteArray())
        }
        socket!!.onmessage = ::onMessage
        socket!!.onclose = {}
    }

    fun register(m: MultiplayerSynchronized) {
        val list = synchronizedObjects[m.topic]
        if (list == null) {
            synchronizedObjects[m.topic] = mutableListOf()
        }
        synchronizedObjects[m.topic]!!.add(m)
    }


    @OptIn(ExperimentalSerializationApi::class)
    fun onMessage(message: MessageEvent) {
        val buffer = message.data.unsafeCast<ArrayBuffer>()
        val decodedMessage = ProtoBuf.decodeFromArrayBuffer<GameMessage>(buffer)
        val messageData = GameMessageData.fromGameMessage(decodedMessage)
        synchronizedObjects[decodedMessage.topic]?.let {
            for (o in it) {
                o.onMessage(messageData, decodedMessage.type)
            }
        }
        Log.d("Websocket", "received $messageData on ${decodedMessage.topic}")
    }
}