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
        particleLifetime = 700f,
        totalLifetime = 1000000.0,
        spawnBurst = 10,
        emitter = CircleEmitter(Vector3(), 3f, Vector3(0, 1, 0)),
        startVelocity = 1f,
        gravity = 50f,
        spawnDelta = 6f,
        startSize = 1.5f,
        endSize = 0f,
        startColor = Color(0xfcecd6),
        endColor = Color(0xfc5f2f),
        startDirectionRandomness = 0.2f,
        particleLifetimeRandomness = 0.3f,
        velocityRandomness = 0.3f
    )

    override val scene = Scene().apply {
        add(particleSystem.renderable)
        add(AmbientLight(color = Color(0xffffff)))
    }

    override fun update(delta: Float) {
        particleSystem.update(delta)
    }
}