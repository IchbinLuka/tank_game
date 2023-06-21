@file:OptIn(ExperimentalSerializationApi::class)

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.reflect.KType
import kotlin.reflect.typeOf


enum class MessageType(val type: KType) {
    Death(typeOf<GameMessageData.Death>()),
    TransformationUpdate(typeOf<GameMessageData.TransformationUpdate>()),
    Spawn(typeOf<GameMessageData.Spawn>()),
    TankCreation(typeOf<GameMessageData.TankCreationData>()),
    GroundData(typeOf<GameMessageData.GroundData>()),
    SessionStateChange(typeOf<GameMessageData.SessionStateUpdate>()),
    ConnectionRequest(typeOf<GameMessageData.ConnectionRequest>()),
    ConnectionResponse(typeOf<GameMessageData.ConnectionResponse>()),
    Disconnect(typeOf<GameMessageData.Disconnect>()),
    GameStateChange(typeOf<GameMessageData.GameStateData>()),
    GameStart(typeOf<GameMessageData.GameStartData>()),
    GameStartRequest(typeOf<GameMessageData.GameStartRequest>()),
    GameObjectMoveUpdate(typeOf<GameMessageData.GameObjectMoveUpdate>()),
    PlayerHealthUpdate(typeOf<GameMessageData.PlayerHealthUpdate>())
}

val typeMessageTypeMap: Map<KType, MessageType> = MessageType.values().associateBy { it.type }

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

        inline fun <reified T: GameMessageData> fromGameMessageData(data: T,topic: String) = GameMessage(
            data = ProtoBuf.encodeToByteArray<T>(data),
            topic = topic,
            type = typeMessageTypeMap[typeOf<T>()]!!
        )
    }
}