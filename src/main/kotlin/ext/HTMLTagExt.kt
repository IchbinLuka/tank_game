package ext

import kotlinx.html.HTMLTag
import util.Localizations

fun HTMLTag.s(key: String, vararg args: Pair<String, Any?>) {
    +Localizations.getString(key, *args)
}