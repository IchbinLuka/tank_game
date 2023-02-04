package scenes

import util.GUIHelper

class LoadingScene: GameScene() {

    override suspend fun loadAssets() {
        GUIHelper.loadGUI("loading_screen")
    }

    override fun update(delta: Double) {}
}