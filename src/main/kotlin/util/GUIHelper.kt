package util

import core.Game
import ext.tryCast
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import kotlinx.html.dom.append
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import ui.UIController

object GUIHelper {

    private val gUIElement: HTMLElement? get() = document.getElementById("uiContainer")?.tryCast()
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
        controllerStack.firstOrNull()?.dispose()
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

    private fun translateDocument() {
        val elements = document.querySelectorAll("[lang-key]")
        for (i in 0 until elements.length) {
            val element = elements[i]
            if (element is HTMLElement) {
                element.innerHTML = Localizations.getString(element.getAttribute("lang-key")!!)
            }
        }
        MainScope().launch { update(controller) }
    }

    private suspend fun update(controller: UIController?) {
        resetGUI()
        if (controller == null) {
            return
        }
        controller.mainUIBuilder?.let {
            gUIElement?.append {
                it(this)
            }
        }

        controller.topBarBuilder?.let {
            topBar?.append {
                it(this)
            }

        }
        controller.init()
        controller.rebuild()
    }


    fun resetGUI() {
        gUIElement?.clear()
        topBar?.clear()
    }
}