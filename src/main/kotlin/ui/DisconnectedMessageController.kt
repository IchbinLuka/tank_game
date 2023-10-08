package ui

import ext.s
import ui.views.backToMenuButton
import ui.views.menuPage

class DisconnectedMessageController : UIController() {
    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = {
                s("disconnected")
            },
            sections = listOf {
                backToMenuButton()
            }
        )
    }
}