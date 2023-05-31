import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val name: String,
    val color: ColorData
)