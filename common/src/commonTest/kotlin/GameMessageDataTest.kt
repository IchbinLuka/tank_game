import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class GameMessageDataTest {
    @Test
    fun vec3Test() {
        val vec = GameMessageData.Vec3Data(Vector3(1.0, 1.0, 1.0))
        val proto = ProtoBuf.encodeToByteArray(vec)
        val decoded: GameMessageData.Vec3Data = ProtoBuf.decodeFromByteArray(proto)
        assertEquals(vec, decoded)
    }

    @Test
    fun emptyTest() {
        val proto = ProtoBuf.encodeToByteArray(GameMessageData.Empty)
        assertEquals(GameMessageData.Empty, ProtoBuf.decodeFromByteArray(proto))
    }
}