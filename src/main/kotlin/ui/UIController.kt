package ui

import org.w3c.dom.Element
import ui.components.UIComponent
import util.GUIHelper

abstract class UIController {
    open val topBarName: String? = null
    open val mainGUIName: String? = null

    open suspend fun init() {}
    open suspend fun initAssets() {}

    suspend fun insertComponent(component: UIComponent, container: Element) {
        component.init()
        container.innerHTML += component.getHTMLString()
        component.onMounted()
        GUIHelper.translateDocument()
    }

    open fun dispose() {}

    abstract fun rebuild()
}