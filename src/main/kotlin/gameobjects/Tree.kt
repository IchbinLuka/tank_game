package gameobjects

import core.CuboidHitBox
import core.HitBox
import core.Hittable
import core.currentScene
import core.gameobjects.GameObject
import ext.TAG
import ext.applyCustomMat
import ext.plus
import gameobjects.bullets.BulletObject
import gameobjects.bullets.BulletTarget
import gameobjects.particles.BasicParticleSystem
import gameobjects.particles.emitter.ConeEmitter
import three.js.*
import util.Log
import util.ResourceManager

class Tree(
    private val startPosition: Vector3,
    override val name: String
): GameObject(), BulletTarget, Hittable {

    override val hitBox: HitBox = CuboidHitBox(
        size = Vector3(x = 3, y = 8, z = 10),
        associatedObject = this,
    ).apply {
        position.y = 4.0
    }

    override val initialChildren: Collection<GameObject> = listOf(hitBox)

    override var renderable: Object3D = Group()
        private set

    override val invincible: Boolean = false
    override var health: Double = 0.03


    override fun onBulletImpact(bullet: BulletObject) {
        Log.d(TAG, "distance: ${bullet.position.distanceTo(position)}")
        die()
    }
    override fun damage(amount: Double) {
        super.damage(amount)
        Log.d(TAG, "got damage: $amount")
    }

    override fun onDeath() {
        super.onDeath()
        currentScene.addGameObject(
            BasicParticleSystem(
                emitter = ConeEmitter(
                    position = position + Vector3(0, 1, 0),
                    radius = 3.0,
                    height = 6.0,
                    seed = 0
                ),
                startDirectionRandomness = 3.0,
                particleLifetimeRandomness = 0.4,
                gravity = 5.0,
                startSize = 3.0,
                endSize = 3.0,
                startColor = Color(0x18b716),
                endColor = Color(0x0e6b0d),
                particleLifetime = 1000.0,
                spawnBurst = 20,
            )
        )
        alive = false
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        val tree = ResourceManager.tree.await().scene.clone()
        tree.applyCustomMat(
            MeshToonMaterial().apply { color = Color(LEAF_COLOR) },
            MeshToonMaterial().apply { color = Color(TRUNK_COLOR) },
        )
        renderable.add(tree)
        position = startPosition
    }

    companion object {
        const val LEAF_COLOR = 0x11710F
        const val TRUNK_COLOR = 0x6D3600
    }
}