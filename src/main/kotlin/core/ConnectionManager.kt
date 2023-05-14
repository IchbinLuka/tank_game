package core

import GameMessage
import GameMessageData
import core.multiplayer.MultiplayerSynchronized
import decodeFromArrayBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import util.Log

@OptIn(ExperimentalSerializationApi::class)
class ConnectionManager(
    url: String
) {



    private val synchronizedObjects = hashMapOf<String, MutableList<MultiplayerSynchronized>>()
    private val socket = WebSocket(url).apply {
        binaryType = BinaryType.ARRAYBUFFER
        onmessage = ::onMessage
    }

    fun register(m: MultiplayerSynchronized) {
        val list = synchronizedObjects[m.topic]
        if (list == null) {
            synchronizedObjects[m.topic] = mutableListOf()
        }
        synchronizedObjects[m.topic]!!.add(m)
    }


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