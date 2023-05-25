@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.reflect.typeOf

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
    object Empty : GameMessageData()

    fun toByteArray(): ByteArray = ProtoBuf.encodeToByteArray(this)

    companion object {
        private inline fun <reified T> decode(array: ByteArray): T = ProtoBuf.decodeFromByteArray<T>(array)

        fun fromByteArray(array: ByteArray) = fromGameMessage(ProtoBuf.decodeFromByteArray(array))

        fun fromGameMessage(message: GameMessage): GameMessageData {
            val type = when (message.type) {
                MessageType.Pos, MessageType.Velocity -> typeOf<Vec3>()
                MessageType.TankCreation -> typeOf<TankCreationData>()
                MessageType.GroundData -> typeOf<GroundData>()
                else -> typeOf<Empty>()
            }
            return ProtoBuf.decodeFromByteArray(serializer(type), message.data) as GameMessageData
        }
    }
}