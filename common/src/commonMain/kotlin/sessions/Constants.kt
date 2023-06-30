package sessions

const val HOST = "localhost"
const val PORT = 8081
const val WEBSOCKET_ROUTE = "sessions/connect"

const val HTTP_URL = "http://${HOST}:${PORT}"
const val WEBSOCKET_URL = "ws://${HOST}:${PORT}/${WEBSOCKET_ROUTE}"

object Topic {
    const val CONNECTION_RESPONSE = "connection_response"
    const val SESSION_STATE = "session_state"
    const val DISCONNECT = "disconnect"
    const val GLOBAL = "global"
    const val MAIN_SCENE = "main_scene"
    const val GROUND = "ground"
}