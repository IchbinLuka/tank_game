package util

import kotlinx.browser.document
import org.w3c.dom.Element

object GUIHelper {

    private val gUIElement: Element? get() = document.getElementById("uiContainer")

    private val cachedGUIs = hashMapOf<String, String>()

    suspend fun loadGUI(name: String) {
        val content = if (cachedGUIs.containsKey(name)) {
            cachedGUIs[name]!!
        } else {
            val s = FileHelper.readFile("GUIs/${name}.html").await()
            cachedGUIs[name] = s
            s
        }
        gUIElement?.innerHTML = content
    }

    fun resetGUI() {
        gUIElement?.innerHTML = ""
    }
}