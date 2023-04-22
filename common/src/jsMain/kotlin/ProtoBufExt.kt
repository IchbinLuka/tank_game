import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ProtoBuf.Default.decodeFromArrayBuffer(buffer: ArrayBuffer): T =
    decodeFromByteArray<T>(Uint8Array(buffer).unsafeCast<ByteArray>())

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ProtoBuf.Default.encodeToArrayBuffer(value: T): ArrayBuffer =
    encodeToByteArray(value).unsafeCast<ArrayBuffer>()