package sessions

const val DEFAULT_HOST = "lukaborn.de"
const val PORT = 8081
const val WEBSOCKET_ROUTE = "sessions/connect"

const val HTTP_URL = "http://${DEFAULT_HOST}:${PORT}"
const val WEBSOCKET_URL = "ws://${DEFAULT_HOST}:${PORT}/${WEBSOCKET_ROUTE}"

object Topic {
    const val CONNECTION_RESPONSE = "connection_response"
    const val SESSION_STATE = "session_state"
    const val DISCONNECT = "disconnect"
    const val GLOBAL = "global"
    const val MAIN_SCENE = "main_scene"
    const val GROUND = "ground"

    fun player(player: String) = "player_$player"
    fun tank(player: String) = "tank_$player"
}