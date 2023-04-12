import kotlinx.serialization.Serializable

@Serializable
data class Vec3(
    val x: Double,
    val y: Double,
    val z: Double
)