package util

import ext.TAG
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object Localizations {

    private const val DEFAULT_LANG = "en-US"

    private val langMutex = Mutex()

    var initialized: Boolean = false
        private set

    private var translations: Map<String, String> = mapOf()

    fun getString(key: String, vararg args: Pair<String, Any?>): String {
        var trans = translations[key] ?: key
        for ((k, v) in args) {
            trans = trans.replace("{$k}", v.toString())
        }
        return trans
    }

    suspend fun load(langCode: String) {
        langMutex.lock()
        val content = FileHelper.readFile("strings/strings_$langCode.json")
        if (content == null) {
            Log.e(TAG, "could not load language with code $langCode")
            return
        }
        try {
            translations = Json.parseToJsonElement(content).jsonObject.toMap().mapValues {
                it.value.jsonPrimitive.content
            }
            initialized = true
        } catch (e: Throwable) {
            Log.e(TAG, "Could not load language. Error: $e")
        } finally {
            langMutex.unlock()
        }
    }

    suspend fun ensureInitialized() {
        langMutex.lock()
        langMutex.unlock()
        if (!initialized) load(DEFAULT_LANG)
    }
}