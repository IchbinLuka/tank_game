package ui

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import ui.components.UIComponent

abstract class UIController {
    open val topBarName: String? = null
    open val mainGUIName: String? = null

    open suspend fun init() {}
    open suspend fun initAssets() {}

    suspend fun insertComponent(component: UIComponent, container: Element) {
        component.init()
        container.innerHTML += component.getHTMLString()
    }

    open fun dispose() {}

    abstract fun rebuild()
}