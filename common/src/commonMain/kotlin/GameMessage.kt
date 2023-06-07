@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

enum class MessageType {
    Death,
    Pos,
    Velocity,
    Spawn,
    TankCreation,
    GroundData,
    Empty,
    SessionStateChange,
    ConnectionRequest,
    ConnectionResponse;
}

@Serializable
data class GameMessage(
    val topic: String,
    val type: MessageType,
    val data: ByteArray
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GameMessage

        if (topic != other.topic) return false
        if (type != other.type) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    fun toByteArray(): ByteArray = ProtoBuf.encodeToByteArray(this)

    companion object {
        fun fromByteArray(bytes: ByteArray): GameMessage = ProtoBuf.decodeFromByteArray(bytes)

        fun fromGameMessageData(data: GameMessageData,type: MessageType, topic: String) = GameMessage(
                data = data.toByteArray(),
                topic =  topic,
                type = type
        )
    }
}