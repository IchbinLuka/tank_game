import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APITokenRequest(val code: String)

@Serializable
data class APITokenResponse(
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    val scope: String
)