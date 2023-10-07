package ui

import core.models.Settings
import ext.fromId
import ext.s
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import services.APIHelper
import ui.views.backButton
import ui.views.menuButton
import ui.views.menuPage
import util.GUIHelper
import util.Localizations

class ServerSettingsController : UIController() {

    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = { s("server_settings") },
            sections = listOf {
                div {
                    style = "display: flex; align-items: center"
                    label("menu-element") {
                        span {
                            s("server_url")
                        }
                        input(type = InputType.text) {
                            id = "hostname"
                            name = "hostname"
                            value = ""
                        }
                    }
                }
                div {
                    div("menu-element") {
                        id = "errorView"
                        style = "visibility: hidden; font-size: 20px; padding: 5px"
                        s("could_not_connect_to_server")
                    }
                }
                div("gridContainer") {
                    style = "grid-template-columns: auto auto auto"
                    backButton()
                    menuButton {
                        s("reset")
                        onClickFunction = { onReset() }
                    }
                    menuButton {
                        s("next")
                        onClickFunction = { onSubmit() }
                    }
                }
            }
        )
    }

    private val hostnameField: HTMLInputElement? get() = Element.fromId("hostname")?.tryCast()

    private val errorElement: HTMLElement? get() = Element.fromId("errorView")?.tryCast()

    override fun rebuild() {}

    private fun updateHost(host: String) {
        MainScope().launch {
            if (APIHelper.updateHost(host)) {
                GUIHelper.pushReplacement(SessionBrowserController())
            } else {
                errorElement?.style?.visibility = "visible"
                errorElement?.innerHTML = Localizations.getString("could_not_connect_to_server")
            }
        }
    }

    private fun onSubmit() {
        if (hostnameField?.value?.isBlank() == true) {
            errorElement?.style?.visibility = "visible"
            errorElement?.innerHTML = Localizations.getString("invalid_server_url")
            return
        }
        updateHost(hostnameField?.value ?: "")
    }

    private fun onReset() {
        updateHost(Settings().server)
    }
}