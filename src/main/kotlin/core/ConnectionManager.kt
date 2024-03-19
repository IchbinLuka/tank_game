@file:OptIn(ExperimentalSerializationApi::class)

package core

import ConnectionResponseStatus
import GameMessage
import GameMessageData
import PlayerData
import core.multiplayer.MessageBroadcaster
import core.multiplayer.MultiplayerSynchronized
import decodeFromArrayBuffer
import exceptions.ConnectionFailedException
import exceptions.InvalidPlayerConfigException
import ext.TAG
import ext.send
import js.core.WeakRef
import kotlinx.browser.window
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import services.APIHelper
import sessions.Topic
import sessions.WEBSOCKET_ROUTE
import util.Log
import kotlin.collections.set
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ConnectionManager : MessageBroadcaster {

    private val synchronizedObjects = hashMapOf<String, MutableList<WeakRef<MultiplayerSynchronized>>>()
    private var socket: WebSocket? = null
    private var shouldReconnect = false

    private val connectMutex = Mutex()

    private val url: String? get() = APIHelper.host?.let { "wss://$it" }

    init {
        window.onbeforeunload = {
            shouldReconnect = false
            socket?.send(
                GameMessage.fromGameMessageData(
                    GameMessageData.Disconnect,
                    Topic.DISCONNECT
                ).toByteArray()
            )
            socket?.close()
            ""
        }
        window.onload = {
            shouldReconnect = false
            ""
        }
    }

    suspend fun connect(sessionId: String, playerData: PlayerData) = connectMutex.withLock {
        suspendCoroutine { continuation ->
            socket = WebSocket("$url/$WEBSOCKET_ROUTE").apply {
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
        socket!!.onclose = {
            Log.d(TAG, "Connection closed")
        }
        suspendCoroutine<Boolean> { continuation ->
            socket!!.send(
                GameMessage.fromGameMessageData(
                    GameMessageData.ConnectionRequest(
                        playerData = playerData,
                        sessionId = sessionId
                    ),
                    Topic.CONNECTION_RESPONSE,
                ).toByteArray()
            )
            socket!!.onmessage = {
                socket!!.onmessage = {}
                // it.data as js.buffer.ArrayBuffer
                val buffer = it.data.unsafeCast<ArrayBuffer>()
                val decodedMessage = ProtoBuf.decodeFromArrayBuffer<GameMessage>(buffer)
                val messageData = GameMessageData.fromGameMessage(decodedMessage)
                if (messageData is GameMessageData.ConnectionResponse) {
                    Log.d(TAG, "Connection response received: ${messageData.successful}")
                    when (messageData.successful) {
                        ConnectionResponseStatus.Success -> {
                            continuation.resume(true)
                        }

                        ConnectionResponseStatus.PlayerConfigUnavailable -> {
                            continuation.resumeWithException(InvalidPlayerConfigException())
                        }

                        ConnectionResponseStatus.Failure -> continuation.resumeWithException(ConnectionFailedException())
                    }

                } else {
                    continuation.resumeWithException(ConnectionFailedException())
                }

            }
        }
        if (socket!!.readyState != WebSocket.OPEN) throw ConnectionFailedException()
        socket!!.onclose = {
            Log.d(TAG, "Connection closed")
        }
        socket!!.onmessage = ::onMessage
    }

    fun disconnect() {
        socket?.onclose = {}
        socket?.close()
        socket = null
    }

    fun register(m: MultiplayerSynchronized) {
        Log.d(TAG, "registering $m on ${m.topic}")
        val list = synchronizedObjects[m.topic]
        if (list == null) {
            synchronizedObjects[m.topic] = mutableListOf()
        }
        synchronizedObjects[m.topic]!!.add(WeakRef(m))
    }

    fun unregister(m: MultiplayerSynchronized) {
        val iterator = synchronizedObjects[m.topic]?.iterator() ?: return
        for (o in iterator) {
            if (o.deref()?.name == m.name) {
                Log.d(TAG, "unregistering ${m.name} on ${m.topic}")

                iterator.remove()
                return
            }
        }
    }

    fun addDisconnectListener(listener: () -> Unit) {
        socket?.addEventListener("close", {
            listener()
        })
    }

    fun removeDisconnectListener(listener: () -> Unit) {
        socket?.removeEventListener("close", {
            listener()
        })
    }


    @OptIn(ExperimentalSerializationApi::class)
    fun onMessage(message: MessageEvent) {
        Log.d(TAG, "Received message, ${message.type}")
        val buffer = message.data.unsafeCast<ArrayBuffer>()
        val decodedMessage = ProtoBuf.decodeFromArrayBuffer<GameMessage>(buffer)
        val messageData = GameMessageData.fromGameMessage(decodedMessage)
        Log.d("Websocket", "received $messageData on ${decodedMessage.topic}")
        synchronizedObjects[decodedMessage.topic]?.let {
            val iterator = it.iterator()
            for (o in iterator) {
                o.deref()?.onMessage(messageData) ?: iterator.remove()
            }
        }
    }

    override fun broadcast(message: GameMessage) {
        socket?.send(message.toByteArray())
    }
}