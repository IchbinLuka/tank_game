package scenes

import core.GameObject
import ext.aspectRatio
import kotlinx.browser.window
import particles.BasicParticleSystem
import particles.emitter.CircleEmitter
import three.js.*

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
        startSize = 0.5,
        endSize = 0.5,
        startColor = Color(0xfcecd6),
        endColor = Color(0xfc5f2f),
        startDirectionRandomness = 0.2,
        particleLifetimeRandomness = 0.3,
        velocityRandomness = 0.3,
        //material = PointsMaterial().apply { vertexColors = false; size = 0.5; color = Color(0xffffff) }
    )

    override val objects = mutableListOf<GameObject>(
        particleSystem,
    )

    override val scene = Scene().apply {
        add(AmbientLight(color = Color(0xffffff)))
        background = Color(0x99f0fc)
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        particleSystem.initVisual()
        particleSystem.renderable?.let { scene.add(it) }
    }

    override fun update(delta: Double) {
        particleSystem.update(delta)
    }
}