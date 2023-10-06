package ui

import ext.addClickListener
import ext.fromId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import util.GUIHelper
import util.PlatformUtil

class GuideController: UIController() {
    override val mainGUIName: String = if (PlatformUtil.isMobile) "guide_mobile" else "guide_desktop"

    private val backButton get() = Element.fromId("backButton")

    override fun rebuild() {}

    override suspend fun init() {
        super.init()
        backButton?.addClickListener {
            MainScope().launch { GUIHelper.pop() }
        }
    }
}