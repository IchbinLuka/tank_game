@file:OptIn(ExperimentalSerializationApi::class)

package core

import GameMessage
import GameMessageData
import MessageType
import PlayerData
import core.multiplayer.MultiplayerSynchronized
import decodeFromArrayBuffer
import exceptions.ConnectionFailedException
import ext.*
import kotlinx.browser.window
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import sessions.Topic
import sessions.WEBSOCKET_URL
import util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ConnectionManager {

    private val synchronizedObjects = hashMapOf<String, MutableList<MultiplayerSynchronized>>()
    private var socket: WebSocket? = null
    private var shouldReconnect = false

    private val connectMutex = Mutex()

    init {
        window.onbeforeunload = {
            shouldReconnect = true
            socket?.send(GameMessage(
                Topic.DISCONNECT,
                MessageType.Disconnect,
                ProtoBuf.encodeToByteArray(GameMessageData.Disconnect)
            ).toByteArray())
            socket?.close()
            ""
        }
        window.onload = {
            shouldReconnect = false
            ""
        }
    }

    suspend fun connect(sessionId: Long, playerData: PlayerData) = connectMutex.withLock {
        suspendCoroutine<Event> { continuation ->
            socket = WebSocket(WEBSOCKET_URL).apply {
                binaryType = BinaryType.ARRAYBUFFER
                onopen = continuation::resume

                onerror = {
                    Log.d(TAG, "Onerror called")
                    continuation.resumeWithException(ConnectionFailedException())
                    socket?.onerror = {}
                    Unit
                }
            }

        }
        socket!!.onerror = {}
        socket!!.onopen = {}
        suspendCoroutine<Boolean> { continuation ->
            socket!!.onclose = {
                continuation.resumeWithException(ConnectionFailedException())
            }
            socket!!.send(
                GameMessage(
                    Topic.CONNECTION_RESPONSE,
                    MessageType.ConnectionRequest,
                    ProtoBuf.encodeToByteArray(
                        GameMessageData.ConnectionRequest(
                            playerData = playerData,
                            sessionId = sessionId
                        )
                    )
                ).toByteArray()
            )
            socket!!.onmessage = {
                socket!!.onmessage = {}
                // it.data as js.buffer.ArrayBuffer
                val buffer = it.data.unsafeCast<ArrayBuffer>()
                val decodedMessage = ProtoBuf.decodeFromArrayBuffer<GameMessage>(buffer)
                val messageData = GameMessageData.fromGameMessage(decodedMessage)
                if (messageData is GameMessageData.ConnectionResponse && messageData.successful) {
                    continuation.resume(true)
                } else {
                    continuation.resumeWithException(ConnectionFailedException())
                }

            }
        }
        socket!!.onclose = {
            Log.d(TAG, "Connection closed")
        }
        socket!!.onmessage = ::onMessage
    }

    fun register(m: MultiplayerSynchronized) {
        Log.d(TAG, "registering $m")
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