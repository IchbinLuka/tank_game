@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.*
import kotlinx.serialization.protobuf.ProtoBuf
import sessions.SessionInfo
import util.Direction

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
    data class CannonUpdateData(
        val angle: Double,
        val direction: Direction
    ) : GameMessageData()

    @Serializable
    data class Explosion(
        val pos: Vec3,
        val strength: Double
    ) : GameMessageData()


    @Serializable
    data class TankCreationData(
        val topic: String,
        val pos: Vec3,
        val name: String,
        val color: ColorData
    ) : GameMessageData()

    @Serializable
    data class GameStateData(
        val tanks: List<TankData>,
        val currentTurn: String
    ) : GameMessageData()

    @Serializable
    data class GameStartData(
        val state: GameStateData,
        val groundData: GroundData,
        val treePositions: List<Double>
    ) : GameMessageData()

    @Serializable
    data class TankData(
        val player: PlayerData,
        val pos: Vec3,
        val rot: Vec3,
        val health: Double
    ) : GameMessageData()

    @Serializable
    data class BulletCreationData(
        val type: String,
        val velocity: Vec3
    ) : GameMessageData()

    @Serializable
    data class BulletImpact(
        val pos: Vec3,
        val intersectingName: String
    ) : GameMessageData()

    @Serializable
    data class GameObjectMoveStart(
        val pos: Vec3,
        val rot: Vec3,
        val velocity: Vec3,
        val acceleration: Vec3
    ) : GameMessageData()

    @Serializable
    data class GameObjectMoveUpdate(
        val pos: Vec3,
        val rot: Vec3,
        val velocity: Vec3,
        val acceleration: Vec3
    ) : GameMessageData()

    @Serializable
    data class GameObjectMoveEnd(
        val pos: Vec3,
        val rot: Vec3,
    ) : GameMessageData()

    @Serializable
    data class ConnectionRequest(
        val playerData: PlayerData,
        val authToken: Long? = null,
        val password: String? = null,
        val sessionId: Long
    ) : GameMessageData()

    @Serializable
    data class GroundDestruction(
        val pos: Double,
        val strength: Double
    ) : GameMessageData()

    @Serializable
    data class ConnectionResponse(
        val successful: Boolean,
        val authToken: Long?
    ): GameMessageData()

    @Serializable
    data class SessionStateUpdate(
        val session: SessionInfo
    ): GameMessageData()

    @Serializable
    data class PlayerHealthUpdate(
        val newHealth: Double
    ) : GameMessageData()

    @Serializable
    data class TransformationUpdate(
        val pos: Vec3,
        val rot: Vec3,
    ) : GameMessageData()

    @Serializable
    object Disconnect : GameMessageData()

    @Serializable
    object Empty : GameMessageData()

    @Serializable
    object Spawn : GameMessageData()

    @Serializable
    object Death : GameMessageData()

    @Serializable
    object GameStartRequest : GameMessageData()

    @Serializable
    data object PingRequest : GameMessageData()

    @Serializable
    data object PingResponse : GameMessageData()

    companion object {
        private inline fun <reified T> decode(array: ByteArray): T = ProtoBuf.decodeFromByteArray<T>(array)

        fun fromByteArray(array: ByteArray) = fromGameMessage(ProtoBuf.decodeFromByteArray(array))

        @OptIn(ExperimentalSerializationApi::class)
        fun fromGameMessage(message: GameMessage): GameMessageData {
            return ProtoBuf.decodeFromByteArray(serializer(message.type.type), message.data) as GameMessageData
        }
    }
}