package util

import ext.TAG
import org.w3c.xhr.XMLHttpRequest

object FileHelper {
    /**
     * Reads file at [path] and returns a Promise containing the file
     *
     * Thanks to https://stackoverflow.com/a/41133213
     */
    suspend fun readFile(path: String): String? {
        val httpRequest = XMLHttpRequest()
        httpRequest.open("GET", path, false)
        httpRequest.send()
        return if (httpRequest.status == 200.toShort()) {
            Log.d(TAG, "Loaded $path")
            httpRequest.responseText
        } else {
            null
        }
    }
}