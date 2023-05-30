package ext

import kotlinx.browser.document
import org.w3c.dom.Element

fun Element.Companion.fromId(id: String): Element? = document.getElementById(id)
