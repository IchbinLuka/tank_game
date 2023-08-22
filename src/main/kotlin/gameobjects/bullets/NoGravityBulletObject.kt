package gameobjects.bullets

import ext.times
import three.js.Vector3

class NoGravityBulletObject(
    args: BulletObjectArgs
) : BulletObject(
    args.copy(
        startVelocity = args.startVelocity.let {
            if (it.length().toDouble() > 20) it else it.normalize() * 20.0
        }
    )
) {
    override var acceleration: Vector3 = Vector3()

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