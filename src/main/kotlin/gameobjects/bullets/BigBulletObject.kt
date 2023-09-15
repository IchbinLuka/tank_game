package gameobjects.bullets

import Bullet
import ext.times
import three.js.Vector3

class BigBulletObject private constructor(
    args: BulletObjectArgs
) : BulletObject(args) {
    override val bulletRadius: Float = 1f

    override val type: Bullet = Bullet.Big

    override val startVelocity: Vector3 = args.startVelocity * 0.6

    companion object: Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return BigBulletObject(arg)
        }
    }
}