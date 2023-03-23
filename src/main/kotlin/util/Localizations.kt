package util

import ext.TAG
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object Localizations {



    private var translations: Map<String, String> = mapOf()

    fun getString(key: String, vararg args: Pair<String, Any?>): String {
        var trans = translations[key] ?: key
        for ((k, v) in args) {
            trans = trans.replace("{$k}", v.toString())
        }
        return trans
    }

    suspend fun load(langCode: String) {
        val content = FileHelper.readFile("strings/strings_$langCode.json")
        if (content == null) {
            Log.e(TAG, "could not load language with code $langCode")
            return
        }
        translations = Json.parseToJsonElement(content).jsonObject.toMap().mapValues {
            it.value.jsonPrimitive.content
        }
    }
}