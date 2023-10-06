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
        val vec = GameMessageData.Spawn
        val message = GameMessage(
            "test", MessageType.Spawn, ProtoBuf.encodeToByteArray(vec)
        )
        val data = ProtoBuf.encodeToByteArray(value = message)
        val decoded = ProtoBuf.decodeFromByteArray<GameMessage>(data)
        assertEquals(message, decoded)
        val decodedMessage = GameMessageData.fromGameMessage(decoded)
        assertIs<GameMessageData.Spawn>(decodedMessage)
        assertEquals(vec, decodedMessage)
    }

    @Test
    fun encodeDecodeGameStart() {
        val ground = GameMessageData.GroundData(
            Array(10) { i -> DoubleArray(10) { j -> (i + j).toDouble() } }
        )
        val data = GameMessageData.GameStartData(
            state = GameMessageData.GameStateData(listOf(), ""),
            groundData = ground,
            trees = listOf()
        )
        val message = GameMessage.fromGameMessageData(data, "")
        assertEquals(message, ProtoBuf.decodeFromByteArray(ProtoBuf.encodeToByteArray(message)))
    }
}