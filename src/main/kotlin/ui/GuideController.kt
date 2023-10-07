package ui

import ext.s
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.style
import ui.views.backButton
import ui.views.menuElement
import ui.views.menuPage
import util.PlatformUtil

class GuideController : UIController() {
    override val mainUIBuilder: UIBuilder = {
        menuPage(
            title = null,
            sections = listOf {
                classes = setOf("scrollableMenuContainer")
                style = "height: 100%; width: 100%"

                val texts = if (PlatformUtil.isMobile) mobileTexts else desktopTexts
                for (text in texts) {
                    menuElement {
                        s(text)
                    }
                }

                div {
                    backButton()
                }
            }
        )
    }

    private val mobileTexts = listOf(
        "guide_move_mobile",
        "guide_aim_mobile",
        "guide_shoot_mobile",
        "guide_select_bullet"
    )

    private val desktopTexts = listOf(
        "guide_move_desktop",
        "guide_aim_desktop",
        "guide_shoot_desktop",
        "guide_select_bullet"
    )
}