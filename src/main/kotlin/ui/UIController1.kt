package ui

import kotlinx.html.DIV

typealias UIBuilder = DIV.() -> Unit
abstract class UIController1 {
    abstract val mainUIBuilder: UIBuilder


}