package util

import core.Game
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import ui.UIController

private const val GUI_LOAD_FAILURE = "Unable to load GUI"

object GUIHelper {

    private val gUIElement: Element? get() = document.getElementById("uiContainer")
    private val topBar: Element? get() = document.getElementById("topBar")

    private val cachedGUIs = hashMapOf<String, String>()

    private val controller: UIController? get() = controllerStack.firstOrNull()
    private var controllerStack: ArrayDeque<UIController> = ArrayDeque()


    fun initListeners() {
        Game.instance.settings.addListener {
            MainScope().launch {
                updateLanguage(it.language)
            }
        }
        MainScope().launch {
            updateLanguage(Game.instance.settings.value.language)
        }
    }

    suspend fun pushReplacement(controller: UIController) {
        val popped = controllerStack.removeFirstOrNull()
        popped?.dispose()
        push(controller)
    }

    suspend fun push(controller: UIController) {
        controllerStack.addFirst(controller)
        update(controller)
    }

    suspend fun pop() {
        val popped = controllerStack.removeFirstOrNull()
        popped?.dispose()
        update(controller)
    }

    suspend fun getContent(name: String): String? = if (cachedGUIs.containsKey(name)) {
        cachedGUIs[name]!!
    } else {
        val s = FileHelper.readFile("GUIs/${name}.html")
        s?.let { cachedGUIs[name] = s }
        s
    }

    private suspend fun updateLanguage(langCode: String) {
        Localizations.load(langCode)
        translateDocument()
    }

    fun translateDocument() {
        val elements = document.querySelectorAll("[lang-key]")
        for (i in 0 until elements.length) {
            val element = elements[i]
            if (element is HTMLElement) {
                element.innerHTML = Localizations.getString(element.getAttribute("lang-key")!!)
            }
        }
        controller?.rebuild()
    }

    private suspend fun update(controller: UIController?) {
        if (controller == null) {
            resetGUI()
            return
        }
        controller.mainGUIName?.let { loadGUI(it) }
        controller.topBarName?.let { loadTopBar(it) }
        translateDocument()
        controller.init()
        controller.rebuild()
    }

    private suspend fun loadGUI(name: String) {
        val content = getContent(name)
        gUIElement?.innerHTML = content ?: GUI_LOAD_FAILURE
    }

    private suspend fun loadTopBar(name: String) {
        topBar?.innerHTML = getContent(name) ?: GUI_LOAD_FAILURE
    }

    fun resetGUI() {
        gUIElement?.innerHTML = ""
        topBar?.innerHTML = ""
    }
}