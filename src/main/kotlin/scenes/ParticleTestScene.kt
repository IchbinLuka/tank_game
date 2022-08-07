package scenes

import core.GameObject
import ext.aspectRatio
import kotlinx.browser.window
import particles.BasicParticleSystem
import particles.CircleEmitter
import three.js.*
import util.launch

class ParticleTestScene: GameScene() {
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 4
    }

    private val particleSystem = BasicParticleSystem(
        particleLifetime = 700.0,
        totalLifetime = 1000000.0,
        spawnBurst = 10,
        emitter = CircleEmitter(Vector3(), 3f, Vector3(0, 1, 0)),
        startVelocity = 1.0,
        gravity = 50.0,
        spawnDelta = 6.0,
        startSize = 1.5,
        endSize = 0.0,
        startColor = Color(0xfcecd6),
        endColor = Color(0xfc5f2f),
        startDirectionRandomness = 0.2,
        particleLifetimeRandomness = 0.3,
        velocityRandomness = 0.3
    )

    override val objects = mutableListOf<GameObject>().apply { add(particleSystem) }

    override val scene = Scene().apply {
        add(AmbientLight(color = Color(0xffffff)))
    }

    override fun update(delta: Double) {
        particleSystem.update(delta)
    }
}