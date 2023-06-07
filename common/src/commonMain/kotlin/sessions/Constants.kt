package sessions

const val HOST = "localhost"
const val PORT = 8081
const val WEBSOCKET_ROUTE  = "/sessions/connect"

const val SERVER_URL = "http://${HOST}/${PORT}"
const val WEBSOCKET_URL = "ws://${HOST}/${PORT}/${WEBSOCKET_ROUTE}"