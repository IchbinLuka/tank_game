package util

import ext.TAG
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.files.FileReader

object FileHelper {

    private val reader = FileReader()

    /**
     * Reads file at [path] and returns a Promise containing the file
     *
     * Thanks to https://stackoverflow.com/a/41133213
     */
    suspend fun readFile(path: String): String? {
        val response = window.fetch(path).await()
        return if (response.status == 200.toShort()) {
            Log.v(TAG, "Loaded $path")
            response.text().await()
        } else {
            Log.e(TAG, "Failed to load $path")
            null
        }
    }
}