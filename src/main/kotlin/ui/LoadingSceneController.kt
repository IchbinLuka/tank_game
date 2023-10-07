package ui

import ui.views.loadingScreen

class LoadingSceneController: UIController() {
    override val mainUIBuilder: UIBuilder = {
        loadingScreen()
    }
}