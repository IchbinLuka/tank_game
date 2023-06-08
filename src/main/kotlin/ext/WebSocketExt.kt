package ext

import org.w3c.dom.WebSocket

@Suppress("NOTHING_TO_INLINE")
inline fun WebSocket.send(message: ByteArray): Unit = asDynamic().send(message)