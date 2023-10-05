package ext

import kotlinx.html.HTMLTag

fun HTMLTag.s(key: String) {
    attributes["lang-key"] = key
}