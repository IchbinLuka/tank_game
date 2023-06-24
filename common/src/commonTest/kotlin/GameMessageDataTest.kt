import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class GameMessageDataTest {
    @Test
    fun vec3Test() {
        val vec = GameMessageData.Vec3(1.0, 1.0, 1.0)
        val proto = ProtoBuf.encodeToByteArray(vec)
        val decoded: GameMessageData.Vec3 = ProtoBuf.decodeFromByteArray(proto)
        assertEquals(vec, decoded)
    }

    @Test
    fun emptyTest() {
        val proto = ProtoBuf.encodeToByteArray(GameMessageData.Empty)
        assertEquals(GameMessageData.Empty, ProtoBuf.decodeFromByteArray(proto))
    }

    @Test
    fun connectionRequestTest() {
        val message = GameMessageData.ConnectionRequest(
            PlayerData("foo", ColorData(9)),
            69,
            "password",
            sessionId = 10
        )
        val proto = ProtoBuf.encodeToByteArray(message)
        assertEquals(message, ProtoBuf.decodeFromByteArray(proto))
    }

    @Test
    fun groundDataTest() {
        val ground = GameMessageData.GroundData(
            Array(100) { i -> DoubleArray(50) { j -> (i + j).toDouble() } }
        )
        val proto = ProtoBuf.encodeToByteArray(ground)
        val decoded = ProtoBuf.decodeFromByteArray<GameMessageData.GroundData>(proto)
        for ((a, b) in decoded.data zip ground.data) {
            assertContentEquals(a, b)
        }
        assertEquals(ground, decoded)
    }

    @Test
    fun gameStartDataTest() {
        val ground = GameMessageData.GroundData(
            Array(10) { i -> DoubleArray(10) { j -> (i + j).toDouble() } }
        )
        val data = GameMessageData.GameStartData(
            state = GameMessageData.GameStateData(listOf(), ""),
            groundData = ground
        )
        val proto = ProtoBuf.encodeToByteArray(data)
        assertEquals(data, ProtoBuf.decodeFromByteArray<GameMessageData.GameStartData>(proto))
    }
}