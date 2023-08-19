package ui

import ColorData
import PlayerData
import exceptions.InvalidPlayerConfigException
import ext.TAG
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import util.GUIHelper
import util.Log

class PlayerConfigurationController(
    private val onSubmitted: suspend (PlayerData) -> Unit,
) : UIController() {
    override val mainGUIName: String = "player_configuration"

    private val nameTextField get() = Element.fromId("player_name") as HTMLInputElement
    private val colorInput get() = Element.fromId("player_color") as HTMLInputElement
    private val backButton get() = Element.fromId("backButton")

    private val submitButton get() = Element.fromId("nextButton")

    private val invalidConfigView: HTMLElement? get() = Element.fromId("invalid_config_view")?.tryCast()

    override suspend fun init() {
        super.init()
        backButton?.addEventListener("click", {
            MainScope().launch { GUIHelper.pop() }
        })
        submitButton?.addEventListener("click", {
            if (nameTextField.value.trim() != "") {
                MainScope().launch {
                    try {
                        onSubmitted(
                            PlayerData(
                                nameTextField.value,
                                ColorData(colorInput.value.substring(1).toInt(16))
                            )
                        )
                    } catch (e: InvalidPlayerConfigException) {
                        Log.e(TAG, "Invalid player config")
                        invalidConfigView?.style?.visibility = "visible"
                    }
                }
            }
        })
    }

    override fun rebuild() {}
}