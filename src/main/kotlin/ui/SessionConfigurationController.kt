package ui

import PlayerData
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import sessions.SessionCreationInfo
import util.GUIHelper

class SessionConfigurationController(
    val player: PlayerData,
    val onSubmit: (SessionCreationInfo) -> Unit
): UIController() {

    override val mainGUIName: String = "session_creation"

    private val nextButton get() = Element.fromId("nextButton")
    private val previousButton get() = Element.fromId("previousButton")

    private val textField get() = Element.fromId("sessionName")?.tryCast<HTMLInputElement>()


    override suspend fun init() {
        super.init()
        nextButton?.addClickListener {
            textField?.value?.let {
                if (it.trim() != "") {
                    onSubmit(SessionCreationInfo(it, player))
                }
            }
        }

        previousButton?.addClickListener {
            MainScope().launch {
                GUIHelper.pop()
            }
        }
    }

    override fun rebuild() {}


}