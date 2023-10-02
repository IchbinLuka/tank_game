package util

/**
 * Thanks to https://stackoverflow.com/a/73625240
 */
inline fun <I> objectOf(
    jsonObject: I = js("new Object()").unsafeCast<I>(),
    writer: I.() -> Unit
): I {
    writer(jsonObject)
    return jsonObject
}