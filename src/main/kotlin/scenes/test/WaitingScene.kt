package scenes.test

import kotlinx.coroutines.delay
import scenes.GameScene

class WaitingScene(
    private val duration: Long = 5000L
): GameScene() {

    override suspend fun preloadAssets() {
        super.preloadAssets()
        delay(duration)
    }

    override fun update(delta: Double) {}
}