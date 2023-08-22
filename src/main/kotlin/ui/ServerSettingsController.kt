package ui

import core.models.Settings
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import services.APIHelper
import util.GUIHelper
import util.Localizations

class ServerSettingsController : UIController() {

    override val mainGUIName: String = "server_settings"

    private val backButton: HTMLButtonElement? get() = Element.fromId("backButton")?.tryCast()
    private val nextButton: HTMLButtonElement? get() = Element.fromId("nextButton")?.tryCast()
    private val resetButton: HTMLButtonElement? get() = Element.fromId("resetButton")?.tryCast()
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

    override suspend fun init() {
        super.init()
        backButton?.addClickListener {
            MainScope().launch {
                GUIHelper.pop()
            }
        }
        nextButton?.addClickListener {
            if (hostnameField?.value?.isBlank() == true) {
                errorElement?.style?.visibility = "visible"
                errorElement?.innerHTML = Localizations.getString("invalid_server_url")
                return@addClickListener
            }
            updateHost(hostnameField?.value ?: "")
        }
        resetButton?.addClickListener {
            updateHost(Settings().server)
        }
    }
}