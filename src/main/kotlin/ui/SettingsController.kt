package ui

import core.Game
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import util.GUIHelper
import util.ScrollableList

class SettingsController: UIController() {

    override val mainGUIName: String = "settings"

    private val langButton by lazy { Element.fromId("languageButton") }
    private val langElement by lazy { Element.fromId("langCode") }
    private val backButton by lazy { Element.fromId("backButton") }

    private val settingsNotifier = Game.instance.settings.apply {
        addListener { rebuild() }
    }

    private var settings by settingsNotifier

    private val locales = ScrollableList(listOf("en", "de"))

    override suspend fun init() {
        super.init()
        langButton?.addEventListener("click", {
            locales.next()
            settings = settings.copy(language = locales.current)
        })
        backButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.pop() }
        })
    }

    override fun rebuild() {
        langElement?.innerHTML = settings.language
    }
}