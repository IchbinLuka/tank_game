package ui

import kotlinx.html.TagConsumer
import org.w3c.dom.HTMLElement

typealias UIBuilder = TagConsumer<HTMLElement>.() -> Unit
abstract class UIController {
    open val mainUIBuilder: UIBuilder? = null
    open val topBarBuilder: UIBuilder? = null

    open fun dispose() {}
    open suspend fun init() {}
    open fun rebuild() {}
}