package types

import kotlin.js.Console

external fun decodeURIComponent(uri: String): String
external fun encodeURIComponent(uri: String): String


inline fun Console.debug(message: Any?): Any? = asDynamic().debug(message)