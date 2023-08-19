import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ColorData(
    val value: Int
) {
    /** Red color component of the color. */
    val r: Int get() = value shr 16 and 0xFF

    /** Green color component of the color. */
    val g: Int get() = value shr 8 and 0xFF

    /** Blue color component of the color. */
    val b: Int get() = value and 0xFF


    fun toVec3() = GameMessageData.Vec3(r / 255.0, g / 255.0, b / 255.0)
}
