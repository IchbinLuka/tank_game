package ui

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.parsing.DOMParser
import ui.components.UIComponent
import util.GUIHelper

abstract class UIController {
    open val topBarName: String? = null
    open val mainGUIName: String? = null

    open suspend fun init() {}

    suspend fun insertComponent(component: UIComponent, container: Element) {
        component.init()
        val node = DOMParser().parseFromString(component.getHTMLString(), "text/html").body?.firstChild as? HTMLElement
            ?: throw IllegalStateException("Could not parse component")
        container.appendChild(node)
        component.onMounted()
        GUIHelper.translateDocument()
    }

    open fun dispose() {}

    abstract fun rebuild()
}