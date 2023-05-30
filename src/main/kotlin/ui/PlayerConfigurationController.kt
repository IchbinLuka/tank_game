package ui

import ColorData
import Player
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import util.GUIHelper

class PlayerConfigurationController(
    private val onSubmitted: (Player) -> Unit
) : UIController() {
    override val mainGUIName: String = "player_configuration"

    private val nameTextField get() = Element.fromId("player_name") as HTMLInputElement
    private val colorInput get() = Element.fromId("player_color") as HTMLInputElement
    private val backButton get() = Element.fromId("backButton")

    private val submitButton get() = Element.fromId("nextButton")

    override suspend fun init() {
        super.init()
        backButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.pop() }
        })
        submitButton?.addEventListener("click", {
            onSubmitted(Player(nameTextField.value, ColorData(colorInput.value.substring(1).toInt(16))))
        })
    }

    override fun rebuild() {}
}