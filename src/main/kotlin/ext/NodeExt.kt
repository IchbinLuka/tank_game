package ext

import kotlinx.dom.clear
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

fun Node.replace(block: TagConsumer<HTMLElement>.() -> Unit) {
    clear()
    append(block)
}