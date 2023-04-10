import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

sealed class GameMessageData {

    @Serializable
    data class Vec3Data(
        val value: Vector3
    ) : GameMessageData()

    @Serializable
    object Empty : GameMessageData()

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun fromGameMessage(message: GameMessage): GameMessageData = when (message.type) {
            MessageType.Pos, MessageType.Velocity -> ProtoBuf.decodeFromByteArray<Vec3Data>(message.data)
            else -> Empty
        }
    }
}