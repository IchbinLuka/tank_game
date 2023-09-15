package gameobjects.bullets

import Bullet
import ext.times
import three.js.Vector3

class NoGravityBulletObject(
    args: BulletObjectArgs
) : BulletObject(args) {
    override val gravity: Vector3 = Vector3(0, 0, 0)

    override val type: Bullet = Bullet.NoGravity

    override val startVelocity: Vector3 = args.startVelocity.let {
        if (it.length().toDouble() > 20) it else it.normalize() * 20.0
    }

    override fun update(delta: Double) {
        super.update(delta)
        if (position.y > 300) {
            despawn()
        }
    }

    companion object: Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return NoGravityBulletObject(arg)
        }
    }
}