package sessions

const val PORT = 8443
const val DEFAULT_HOST = "lukaborn.de:$PORT"
const val WEBSOCKET_ROUTE = "sessions/connect"

const val WEBSOCKET_URL = "ws://${DEFAULT_HOST}/${WEBSOCKET_ROUTE}"

const val DISCORD_CLIENT_ID = "1219342090610278500"

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