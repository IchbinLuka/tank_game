package generated

@kotlinx.serialization.Serializable
data class ObjectConfiguration(
    val items: ArrayList<ObjectConfigurationItem>
)

@kotlinx.serialization.Serializable
data class ObjectConfigurationItem(
    val name: String,
    val pos: Pos,
    val rot: Rot
)

@kotlinx.serialization.Serializable
data class Pos(
    val x: Double,
    val y: Double,
    val z: Double
)

@kotlinx.serialization.Serializable
data class Rot(
    val x: Double,
    val y: Double,
    val z: Double
)