package ext

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

fun Element.Companion.fromId(id: String): Element? = document.getElementById(id)

fun Element.addClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)
fun Element.removeClickListener(listener: (Event) -> Unit) = removeEventListener("click", listener)