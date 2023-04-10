import kotlinx.serialization.Serializable

enum class MessageType() {
    Death,
    Pos,
    Velocity,
    Spawn
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
}