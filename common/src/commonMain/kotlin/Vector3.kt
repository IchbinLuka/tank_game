import kotlinx.serialization.Serializable

@Serializable
data class Vector3(
    val x: Double,
    val y: Double,
    val z: Double
)