package util

import org.w3c.files.File
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise

object FileHelper {
    /**
     * Reads file at [path] and returns a Promise containing the file
     *
     * Thanks to https://stackoverflow.com/a/41133213
     */
    fun readFile(path: String): Promise<String> = Promise<String>() {
        resolve, reject ->
        val httpRequest = XMLHttpRequest()
        httpRequest.open("GET", path, false)
        httpRequest.send()
        if (httpRequest.status == 200.toShort()) {
            resolve(httpRequest.responseText)
        } else {
            reject(Exception())
        }
    }
}