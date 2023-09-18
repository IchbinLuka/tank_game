package ui

import ext.TAG
import ext.fromId
import org.w3c.dom.Element
import ui.components.JoyStickComponent
import util.Log

class JoyStickTestController : UIController() {
    override val mainGUIName: String = "joy_stick_test"

    override suspend fun init() {
        super.init()
        insertComponent(

            JoyStickComponent {
                Log.d(TAG, "JoyStickComponent: ${it.x} ${it.y}")
            },
            Element.fromId("joystick_container")!!
        )
    }

    override fun rebuild() {}
}