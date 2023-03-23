package scenes

import ui.LoadingSceneController
import util.GUIHelper

class LoadingScene: GameScene() {

    override suspend fun loadAssets() {
        GUIHelper.pushReplacement(LoadingSceneController())
    }

    override fun update(delta: Double) {}
}