package sessions

import PlayerData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SessionCreationInfo(
        val name: String,
        val host: PlayerData,
        val password: String? = null
)

@Serializable
class SessionInfo(
    val id: Long,
    @Transient val password: String? = null,
    val name: String,
    val host: PlayerData
) {
    val players: MutableList<PlayerData> = mutableListOf(host)
}