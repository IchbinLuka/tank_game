import org.w3c.dom.WebSocket
import util.Log

fun initWebSocket() {
    val socket = WebSocket("ws://127.0.0.1/ws")
    socket.onmessage = {
        Log.d("Websocket", it.data)
    }
    socket.onopen = {

    }
}