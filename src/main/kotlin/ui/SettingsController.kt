package ui

import core.Game
import ext.fromId
import ext.tryCast
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import util.FullscreenUtil
import util.GUIHelper
import util.ScrollableList

class SettingsController: UIController() {

    override val mainGUIName: String = "settings"

    private val langButton by lazy { Element.fromId("languageButton") }
    private val langElement by lazy { Element.fromId("langCode") }
    private val backButton by lazy { Element.fromId("backButton") }
    private val fullscreenCheckBox: HTMLInputElement? by lazy {
        Element.fromId("fullscreen_checkbox")?.tryCast()
    }

    private val settingsNotifier = Game.instance.settings.apply {
        addListener { rebuild() }
    }

    private var settings by settingsNotifier

    private val locales = ScrollableList(listOf("en", "de")).apply {
        nextWhile { it != settings.language }
    }


    override suspend fun init() {
        super.init()

        langButton?.addEventListener("click", {
            locales.next()
            settings = settings.copy(language = locales.current)
        })
        backButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.pop() }
        })
        fullscreenCheckBox?.addEventListener("click", {
            fullscreenCheckBox?.checked?.let {
                FullscreenUtil.setFullscreen(it)
            }
        })
        document.addEventListener("fullscreenchange", {
            fullscreenCheckBox?.checked = document.fullscreenElement != null
        })
    }

    override fun rebuild() {
        langElement?.innerHTML = settings.language
    }
}