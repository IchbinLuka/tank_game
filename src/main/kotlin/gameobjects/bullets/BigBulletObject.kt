package gameobjects.bullets

import ext.times
import gameobjects.Tank
import three.js.Vector3

class BigBulletObject private constructor(
    args: BulletObjectArgs
) : BulletObject(args.copy(
    startVelocity = args.startVelocity * 0.6
)) {
    override val bulletRadius: Float = 1f

    override val explosionWidth: Double = 60.0

    override val damage: Double = 0.7

    companion object: Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return BigBulletObject(arg)
        }
    }
}