@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

sealed class GameMessageData {

    @Serializable
    data class Vec3Data(
        val value: Vec3
    ) : GameMessageData()

    @Serializable
    object Empty : GameMessageData()

    companion object {
        fun fromByteArray(array: ByteArray) = fromGameMessage(ProtoBuf.decodeFromByteArray(array))
        fun fromGameMessage(message: GameMessage): GameMessageData = when (message.type) {
            MessageType.Pos, MessageType.Velocity -> ProtoBuf.decodeFromByteArray<Vec3Data>(message.data)
            else -> Empty
        }
    }
}