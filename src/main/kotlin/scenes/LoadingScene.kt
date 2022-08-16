package scenes

import core.gameobjects.GameObject
import util.GUIHelper

class LoadingScene: GameScene() {

    override val objects: Collection<GameObject> = listOf()

    override suspend fun loadAssets() {
        GUIHelper.loadGUI("loading_screen")
    }

    override fun update(delta: Double) {}
}