import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalSerializationApi::class)
class GameMessageTest {
    @Test
    fun encodeDecode() {
        val vec = GameMessageData.Vec3(1.0, 1.0, 1.0)
        val message = GameMessage(
            "test", MessageType.Pos, ProtoBuf.encodeToByteArray(vec)
        )
        val data = ProtoBuf.encodeToByteArray(value = message)
        val decoded = ProtoBuf.decodeFromByteArray<GameMessage>(data)
        assertEquals(message, decoded)
        val decodedMessage = GameMessageData.fromGameMessage(decoded)
        assertIs<GameMessageData.Vec3>(decodedMessage)
        assertEquals(vec, decodedMessage)
    }
}