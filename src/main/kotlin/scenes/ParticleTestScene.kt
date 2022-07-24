package scenes

import core.GameScene
import ext.aspectRatio
import kotlinx.browser.window
import particles.BasicParticleSystem
import particles.CircleEmitter
import particles.SquareEmitter
import three.js.*

class ParticleTestScene: GameScene() {
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4
    }
    private val particleSystem = BasicParticleSystem(
        particleLifetime = 30000f,
        totalLifetime = 1000000.0,
        spawnBurst = 10,
        emitter = CircleEmitter(Vector3(), 2f, Vector3(0, 1, 0)),
        startVelocity = 0f,
        gravity = 0f,
        spawnDelta = 100f,
        startSize = 0.5f,
        endSize = 0f,
        startColor = Color(0xff0000),
    )

    override val scene = Scene().apply {
        add(particleSystem.renderable)
        add(AmbientLight(color = Color(0xffffff)))
    }

    override fun update(delta: Float) {
        particleSystem.update(delta / 1000)
    }
}