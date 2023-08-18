package gameobjects.bullets

import Bullet
import GameMessageData
import core.Hittable
import ext.TAG
import util.Log

class TeleportBullet private constructor(args: BulletObjectArgs) : BulletObject(args) {

    override val type: Bullet = Bullet.Teleport
    override fun onIntersectionEnter(intersectingBox: Hittable) {
        tank.position.x = position.x
        tank.adjustTransformToGround(ground)
        broadcastImpact(intersectingBox)
        die()
    }

    init {
        Log.d(TAG, "TeleportBullet created")
    }

    override fun onMessage(message: GameMessageData) {
        super.onMessage(message)
    }

    companion object: BulletObject.Factory() {
        override fun createGameObject(arg: BulletObjectArgs): BulletObject {
            return TeleportBullet(arg)
        }
    }
}