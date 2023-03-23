package ui

abstract class UIController {
    open val topBarName: String? = null
    open val mainGUIName: String? = null

    open suspend fun init() {}
    open suspend fun initAssets() {}

    open fun dispose() {}

    abstract fun rebuild()
}