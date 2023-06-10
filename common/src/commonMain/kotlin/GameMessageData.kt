@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import sessions.SessionInfo
import kotlin.reflect.typeOf

@Serializable
sealed class GameMessageData {

    @Serializable
    data class Vec3(
        val x: Double,
        val y: Double,
        val z: Double
    ) : GameMessageData()

    @Serializable
    data class GroundData(
        val data: Array<DoubleArray>
    ) : GameMessageData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as GroundData

            return data.contentDeepEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentDeepHashCode()
        }
    }


    @Serializable
    data class TankCreationData(
        val pos: Vec3,
        val name: String,
        val color: ColorData
    ) : GameMessageData()

    @Serializable
    data class ConnectionRequest(
        val playerData: PlayerData,
        val password: String? = null,
        val sessionId: Long
    ) : GameMessageData()

    @Serializable
    data class ConnectionResponse(
        val successful: Boolean
    ): GameMessageData()

    @Serializable
    data class SessionStateUpdate(
        val session: SessionInfo
    ): GameMessageData()

    @Serializable
    object Empty : GameMessageData()

    companion object {
        private inline fun <reified T> decode(array: ByteArray): T = ProtoBuf.decodeFromByteArray<T>(array)

        fun fromByteArray(array: ByteArray) = fromGameMessage(ProtoBuf.decodeFromByteArray(array))

        @OptIn(ExperimentalSerializationApi::class)
        fun fromGameMessage(message: GameMessage): GameMessageData {
            val type = when (message.type) {
                MessageType.Pos, MessageType.Velocity -> typeOf<Vec3>()
                MessageType.TankCreation -> typeOf<TankCreationData>()
                MessageType.GroundData -> typeOf<GroundData>()
                MessageType.ConnectionRequest -> typeOf<ConnectionRequest>()
                MessageType.ConnectionResponse -> typeOf<ConnectionResponse>()
                MessageType.SessionStateChange -> typeOf<SessionStateUpdate>()
                else -> typeOf<Empty>()
            }
            return ProtoBuf.decodeFromByteArray(serializer(type), message.data) as GameMessageData
        }
    }
}