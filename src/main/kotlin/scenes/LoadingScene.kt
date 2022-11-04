package scenes

import core.gameobjects.GameObject
import util.GUIHelper

class LoadingScene: GameScene() {

    override val objects: MutableSet<GameObject> = hashSetOf()

    override suspend fun loadAssets() {
        GUIHelper.loadGUI("loading_screen")
    }

    override fun update(delta: Double) {}
}