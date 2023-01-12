package util

import ext.TAG
import org.w3c.files.FileReader
import org.w3c.xhr.XMLHttpRequest

object FileHelper {

    private val reader = FileReader()

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
            Log.v(TAG, "Loaded $path")
            httpRequest.responseText
        } else {
            Log.e(TAG, "Failed to load $path")
            null
        }
    }
}