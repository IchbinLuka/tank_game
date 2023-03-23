package core.models

@kotlinx.serialization.Serializable
data class Settings(
    val language: String = "en"
) {
    companion object {
        fun fromCookies(): Settings = TODO("Not yet implemented")
    }
}