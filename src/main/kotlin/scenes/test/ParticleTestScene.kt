package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import gameobjects.particles.BasicParticleSystem
import gameobjects.particles.emitter.ConeEmitter
import kotlinx.browser.window
import scenes.GameScene
import three.js.*

class ParticleTestScene(
    private val particleSystem: BasicParticleSystem = BasicParticleSystem(
        particleLifetime = 600.0,
        spawnBurst = 100,
        emitter = ConeEmitter(
            position = Vector3(0, 0, 0),
            radius = 2.0,
            height = 10.0,
            seed = 0
        ),
        startVelocity = 0.0,
        startSize = 0.6,
        endSize = 0.6,
        gravity = 0.0,
        spawnDelta = 1.0,
        startColor = Color(0xfcd125),
        endColor = Color(0xfc3a0f),
        startDirectionRandomness = 0.2,
        particleLifetimeRandomness = 0.3,
        velocityRandomness = 0.0,
    )
): GameScene() {
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 20.0
    }

    override val initialChildren: Collection<GameObject> = listOf(particleSystem)

    override val scene = Scene().apply {
        add(AmbientLight(color = Color(0xffffff)))
        background = Color(0x99f0fc)
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        particleSystem.preloadAssets()
        particleSystem.renderable?.let { scene.add(it) }
    }

    override fun update(delta: Double) {
        particleSystem.update(delta)
    }
}