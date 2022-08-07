package scenes

import core.GameObject
import util.GUIHelper
import util.launch

class LoadingScene: GameScene() {

    override val objects: Collection<GameObject> = listOf()

    override suspend fun loadAssets() {
        GUIHelper.loadGUI("loading_screen")
    }

    init {
        launch { loadAssets() }
    }

    override fun update(delta: Double) {}
}