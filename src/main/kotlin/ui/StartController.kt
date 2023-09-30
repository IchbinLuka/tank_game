package ui

import ColorData
import PlayerData
import core.Game
import createGame
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import scenes.MainSceneSingleplayer
import services.APIHelper
import util.GUIHelper
import util.Localizations

class StartController: UIController() {

    override val mainGUIName: String = "start"

    override suspend fun init() {
        super.init()
        initListeners()
        MainScope().launch {
            if (APIHelper.host == null) return@launch
            multiplayerButton?.disabled = true
            if (!APIHelper.serverAvailable()) {
                APIHelper.resetHost()
            }
            multiplayerButton?.disabled = false
        }
    }

    override fun rebuild() {}

    private val multiplayerButton: HTMLButtonElement? get() = Element.fromId("multiplayerButton")?.tryCast()
    private val localButton: HTMLButtonElement? get() = Element.fromId("localButton")?.tryCast()
    private val guideButton: HTMLButtonElement? get() = Element.fromId("guideButton")?.tryCast()


    private fun initListeners() {
        Element.fromId("settingsButton")?.addEventListener("click", {
            MainScope().launch { GUIHelper.push(SettingsController()) }
        })
        multiplayerButton?.addClickListener {
            if (APIHelper.url == null) {
                MainScope().launch { GUIHelper.push(ServerSettingsController()) }
            } else {
                MainScope().launch { GUIHelper.push(SessionBrowserController()) }
            }
        }
        localButton?.addClickListener {
            MainScope().launch { GUIHelper.push(LocalGameStart()) }
        }

        guideButton?.addClickListener {
            MainScope().launch { GUIHelper.push(GuideController()) }
        }
    }
}