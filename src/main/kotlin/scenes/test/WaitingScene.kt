package scenes.test

import core.gameobjects.GameObject
import kotlinx.coroutines.delay
import scenes.GameScene

class WaitingScene(
    private val duration: Long = 5000L
): GameScene() {
    override val children: HashSet<GameObject>
        get() = hashSetOf()

    override suspend fun preloadAssets() {
        super.preloadAssets()
        delay(duration)
    }

    override fun update(delta: Double) {}
}