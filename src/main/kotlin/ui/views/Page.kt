package ui.views

import core.Game
import ext.s
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import scenes.StartScene
import util.GUIHelper

typealias UI<T> = TagConsumer<T>

fun <T> UI<T>.menuPage(
    title: (H1.() -> Unit)? = null,
    sections: List<DIV.() -> Unit> = listOf()
): T = div {
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

fun <T> UI<T>.menuElement(context: DIV.() -> Unit): T = div("menu-element") {
    context()
}

fun <T> UI<T>.menuButton(content: BUTTON.() -> Unit): T =
    button(classes = "menu-element button") {
        content()
    }

fun <T> UI<T>.backButton(): T = menuButton {
    onClickFunction = {
        MainScope().launch { GUIHelper.pop() }
    }
    s("back")
}

fun <T> UI<T>.backToMenuButton(): T = menuButton {
    s("return_to_menu")
    onClickFunction = {
        Game.instance.currentScene = StartScene()
    }
}

fun <T> UI<T>.loadingIndicator(content: SPAN.() -> Unit = {}) = span(classes = "loader") {
    content()
}