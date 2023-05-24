package gameobjects.bullets

import core.Hittable
import gameobjects.GameObjectFactory
import gameobjects.Tank
import three.js.Vector3

class TeleportBullet private constructor(args: BulletObjectArgs) : BulletObject(args) {

    override val explosionWidth: Double = 0.0
    override val damage: Double = 0.0
    override fun onIntersectionEnter(intersectingBox: Hittable) {
        tank.position.x = position.x
        tank.adjustTransformToGround(ground)
        die()
    }

    companion object: BulletObject.Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return TeleportBullet(arg)
        }
    }
}