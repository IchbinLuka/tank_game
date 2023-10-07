package ui

import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import services.APIHelper
import ui.views.startScreen
import util.GUIHelper

class StartController: UIController() {


    override suspend fun init() {
        super.init()
        MainScope().launch {
            if (APIHelper.host == null) return@launch
            multiplayerButton?.disabled = true
            if (!APIHelper.serverAvailable()) {
                APIHelper.resetHost()
            }
            multiplayerButton?.disabled = false
        }
    }


    private val multiplayerButton: HTMLButtonElement? get() = Element.fromId("multiplayerButton")?.tryCast()


    override val mainUIBuilder: UIBuilder = {
        startScreen(
            onLocalGame = {
                MainScope().launch { GUIHelper.push(LocalGameStart()) }
            },
            onMultiplayerGame = {
                MainScope().launch { GUIHelper.push(SessionBrowserController()) }
            },
            onGuide = {
                MainScope().launch { GUIHelper.push(GuideController()) }
            },
            onSettings = {
                MainScope().launch { GUIHelper.push(SettingsController()) }
            }
        )
    }
}