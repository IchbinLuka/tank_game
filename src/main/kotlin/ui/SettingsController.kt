package ui

import core.Game
import ext.fromId
import ext.s
import ext.tryCast
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import ui.views.backButton
import ui.views.menuButton
import ui.views.menuElement
import ui.views.menuPage
import util.FullscreenUtil
import util.ScrollableList

class SettingsController: UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("settings") },
            sections = listOf {
                style = "flex-grow: 3"

                div("gridContainer") {
                    menuButton {
                        id = "languageButton"
                        style = "cursor: pointer"

                        onClickFunction = { langButtonClick() }

                        span {
                            s("language")
                        }
                        span {
                            +settings.language
                        }
                    }
                    if (document.fullscreenEnabled) {
                        menuElement {
                            label {
                                span {
                                    s("fullscreen")
                                }
                                input(classes = "checkBox", type = InputType.checkBox) {
                                    id = "fullscreen_checkbox"
                                    checked = isFullscreen
                                    onClickFunction = { fullscreenClick() }
                                }
                            }
                        }
                    }
                }

                backButton()
            }
        )
    }

    private val fullscreenCheckBox: HTMLInputElement? by lazy {
        Element.fromId("fullscreen_checkbox")?.tryCast()
    }

    private val settingsNotifier = Game.instance.settings.apply {
        addListener { rebuild() }
    }

    private var settings by settingsNotifier

    private val locales = ScrollableList(listOf("en-US", "de")).apply {
        nextWhile { it != settings.language }
    }

    private fun langButtonClick() {
        locales.next()
        settings = settings.copy(language = locales.current)
    }

    private fun fullscreenClick() {
        fullscreenCheckBox?.checked?.let {
            FullscreenUtil.setFullscreen(it)
        }
    }

    override suspend fun init() {
        super.init()
        document.addEventListener("fullscreenchange", {
            fullscreenCheckBox?.checked = isFullscreen
        })
    }

    private val isFullscreen: Boolean get() = document.fullscreenElement != null
}