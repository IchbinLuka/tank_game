import kotlinx.serialization.Serializable

@Serializable
data class ColorData(
    val r: Int,
    val g: Int,
    val b: Int
)
