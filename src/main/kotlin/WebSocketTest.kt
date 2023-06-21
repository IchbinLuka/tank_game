@file:OptIn(ExperimentalSerializationApi::class)

import ext.send
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.WebSocket
import sessions.WEBSOCKET_URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun initWebSocket() {
    val socket = WebSocket(WEBSOCKET_URL)
    val message = ProtoBuf.encodeToByteArray(
        GameMessage(
            "topic",
            MessageType.Spawn,
            data = ProtoBuf.encodeToByteArray(
                GameMessageData.Spawn
            )
        )
    )
    suspendCoroutine<Unit> { continuation ->

        socket.binaryType = BinaryType.ARRAYBUFFER
        socket.onmessage = {

            // Log.d("Websocket", it.data)
        }

        socket.onerror = {
            continuation.resume(Unit)
        }
        socket.onopen = {
            socket.send(message)
            continuation.resume(Unit)
        }
    }
    // socket.send(message)
}
