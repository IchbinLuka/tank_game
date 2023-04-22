package core

import GameMessage
import GameMessageData
import core.multiplayer.MultiplayerSynchronized
import decodeFromArrayBuffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.MessageEvent
import util.Log

@OptIn(ExperimentalSerializationApi::class)
class ConnectionManager {

    private val synchronizedObjects = hashMapOf<String, MutableList<MultiplayerSynchronized>>()

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
                o.onMessage(messageData)
            }
        }
        Log.d("Websocket", "received $messageData on ${decodedMessage.topic}")
    }
}