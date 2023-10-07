package ui.views

import ext.s
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import util.GUIHelper

typealias UI = TagConsumer<HTMLElement>

fun UI.menuPage(title: (H1.() -> Unit)? = null, sections: List<DIV.() -> Unit> = listOf()) {
    div {
        style = "width: 100%; height: 100%"
        div("center menuPage") {
            title?.let {
                div("menuContainer") {
                    h1("menu-element menuTitle") { title() }
                }
            }
            for (section in sections) {
                div("menuContainer") {
                    section()
                }
            }
        }
    }
}

fun UI.menuElement(context: DIV.() -> Unit) {
    div("menu-element") {
        context()
    }
}

fun UI.menuButton(content: BUTTON.() -> Unit) {
    button(classes = "menu-element button") {
        content()
    }
}

fun UI.backButton() {
    menuButton {
        onClickFunction = {
            MainScope().launch { GUIHelper.pop() }
        }
        s("back")
    }
}

fun UI.loadingIndicator(content: SPAN.() -> Unit = {}) {
    span(classes = "loader") {
        content()
    }
}