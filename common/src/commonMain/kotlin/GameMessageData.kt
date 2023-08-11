@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
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
        val health: Double,
        val bulletCounts: BulletCounts
    ) : GameMessageData()

    @Serializable
    data class BulletCreationData(
        val type: Bullet,
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
    data class MessageRefusal(
        val reason: String,
        val message: GameMessage
    ) : GameMessageData()

    @Serializable
    data class BulletCounts(
        val counts: Array<Int?>
    ) : GameMessageData() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as BulletCounts

            return counts.contentEquals(other.counts)
        }

        override fun hashCode(): Int {
            return counts.contentHashCode()
        }

        /**
         * Returns a map where the keys are the Bullets and the values are the corresponding counts.
         *
         * @return a map with the Bullets as keys and their counts as values
         */
        fun getCountsMap(): Map<Bullet, Int?> {
            val entries = Bullet.entries.toTypedArray()
            return counts.mapIndexedNotNull { index, i -> entries[index] to i }.toMap()
        }

        companion object {
            /**
             * Creates a new BulletCounts object from a map where the keys are the Bullets and the values are the
             * corresponding counts.
             *
             * @param map a map with the Bullets as keys and their counts as values
             * @return a new BulletCounts object
             */
            fun fromMap(map: Map<Bullet, Int?>): BulletCounts {
                val entries = Bullet.entries.toTypedArray()
                return BulletCounts(entries.map { map[it] }.toTypedArray())
            }
        }
    }

    @Serializable
    data object Disconnect : GameMessageData()

    @Serializable
    data object Empty : GameMessageData()

    @Serializable
    data object Spawn : GameMessageData()

    @Serializable
    data object Death : GameMessageData()

    @Serializable
    data object GameStartRequest : GameMessageData()

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