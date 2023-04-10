import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class GameMessageTest {
    @Test
    fun encodeDecode() {
        val message = GameMessage(
            "test", MessageType.Pos, ByteArray(10)
        )
        val data = ProtoBuf.encodeToByteArray(value = message)
        val decoded = ProtoBuf.decodeFromByteArray<GameMessage>(data)
        assertEquals(message, decoded)
    }
}