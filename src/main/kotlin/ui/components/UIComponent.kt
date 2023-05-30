package ui.components

import util.GUIHelper

abstract class UIComponent {
    abstract val componentSource: String

    protected open val replacements: HashMap<String, String> = hashMapOf()

    private var htmlString: String? = null

    open suspend fun init() {
        htmlString = GUIHelper.getContent("components/$componentSource") ?: "Error"
    }

    open fun onMounted() {

    }

    open fun getHTMLString(): String {
        if (htmlString == null) {
            throw IllegalStateException("Component is not initialized")
        }
        var result = htmlString!!
        for ((key, value) in replacements) {
            result = result.replace("{{$key}}", value)
        }
        return result
    }
}