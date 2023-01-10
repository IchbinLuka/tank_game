package util

import kotlinx.browser.document
import org.w3c.dom.Element

private const val GUILoadFailure = "Unable to load GUI"

object GUIHelper {

    private val gUIElement: Element? get() = document.getElementById("uiContainer")
    private val topBar: Element? get() = document.getElementById("topBar")

    private val cachedGUIs = hashMapOf<String, String>()

    private suspend fun getContent(name: String) = if (cachedGUIs.containsKey(name)) {
        cachedGUIs[name]!!
    } else {
        val s = FileHelper.readFile("GUIs/${name}.html")
        s?.let { cachedGUIs[name] = s }
        s
    }

    suspend fun loadGUI(name: String) {
        val content = getContent(name)
        gUIElement?.innerHTML = content ?: GUILoadFailure
    }

    suspend fun loadTopBar(name: String) {
        topBar?.innerHTML = getContent(name) ?: GUILoadFailure
    }

    fun resetGUI() {
        gUIElement?.innerHTML = ""
        topBar?.innerHTML = ""
    }
}