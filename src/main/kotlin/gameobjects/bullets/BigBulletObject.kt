package gameobjects.bullets

import Bullet
import ext.times

class BigBulletObject private constructor(
    args: BulletObjectArgs
) : BulletObject(args.copy(
    startVelocity = args.startVelocity * 0.6
)) {
    override val bulletRadius: Float = 1f

    override val type: Bullet = Bullet.Big

    companion object: Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return BigBulletObject(arg)
        }
    }
}