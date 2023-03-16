package gameobjects.bullets

import core.Hittable
import gameobjects.Tank
import three.js.Vector3

class TeleportBullet(startVelocity: Vector3, tank: Tank) : BulletObject(startVelocity, tank) {

    override val explosionWidth: Double = 0.0
    override val damage: Double = 0.0
    override fun onIntersectionEnter(intersectingBox: Hittable) {
        tank.position.x = position.x
        tank.adjustTransformToGround(ground)
        die()
    }
}