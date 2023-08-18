package gameobjects

import core.CuboidHitBox
import core.HitBox
import core.Hittable
import core.gameobjects.GameObject
import ext.TAG
import ext.applyCustomMat
import gameobjects.bullets.BulletObject
import gameobjects.bullets.BulletTarget
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
        addIntersectionListener {
            if (it is Tank) {
                // this@Tree.die() TODO
            }
        }
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
        alive = false
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()
        val tree = ResourceManager.tree.await().scene.clone()
        tree.applyCustomMat(
            MeshToonMaterial().apply { color = Color(0x11710F) },
            MeshToonMaterial().apply { color = Color(0x6D3600) },
        )
        renderable.add(tree)
        position = startPosition
    }
}