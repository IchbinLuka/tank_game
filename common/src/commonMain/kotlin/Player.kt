import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val name: String,
    val color: ColorData
)