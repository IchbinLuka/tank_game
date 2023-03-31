package scenes.test

import core.gameobjects.GameObject
import ext.aspectRatio
import gameobjects.particles.BasicParticleSystem
import gameobjects.particles.emitter.SphereEmitter
import kotlinx.browser.window
import scenes.GameScene
import three.js.*

class ParticleTestScene(
    private val particleSystem: BasicParticleSystem = BasicParticleSystem(
        particleLifetime = 600.0,
        spawnBurst = 100,
        emitter = SphereEmitter(Vector3(z = -50), 2.0, 0),
        startVelocity = 10.0,
        startSize = 2.0,
        endSize = 10.0,
        startColor = Color(0xfcd125),
        endColor = Color(0xfc3a0f),
        startDirectionRandomness = 0.2,
        particleLifetimeRandomness = 0.3,
        velocityRandomness = 0.3,
    )
): GameScene() {
    override val camera: Camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 20.0
    }

    override val _children: MutableMap<String, GameObject> = hashMapOf(
        "particleSystem" to particleSystem
    )

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