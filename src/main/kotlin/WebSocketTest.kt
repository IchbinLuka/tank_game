@file:OptIn(ExperimentalSerializationApi::class)

import ext.send
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.w3c.dom.WebSocket
import util.Log

fun initWebSocket() {
    val socket = WebSocket("ws://127.0.0.1:8081/ws")
    socket.onmessage = {
        Log.d("Websocket", it.data)
    }
    val message = ProtoBuf.encodeToByteArray(
        GameMessage(
            "topic",
            MessageType.Pos,
            data = ProtoBuf.encodeToByteArray(
                GameMessageData.Vec3Data(
                    Vec3(1.0, 1.0, 1.0)
                )
            )
        )
    )
    socket.onopen = {
        socket.send(message)
    }
}
