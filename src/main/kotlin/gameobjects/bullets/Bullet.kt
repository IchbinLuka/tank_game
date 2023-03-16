package gameobjects.bullets

import gameobjects.Tank
import three.js.Vector3

typealias BulletObjectFactory = (velocity: Vector3, tank: Tank) -> BulletObject

enum class Bullet(
    val createObject: BulletObjectFactory
) {
    Normal({ v, t ->
        BulletObject(tank = t, startVelocity = v)
    }),
    NoGravity({ v, t ->
        NoGravityBulletObject(tank = t, startVelocity = v)
    }),
    Big({ v, t  ->
        BigBulletObject(tank = t, startVelocity = v)
    }),
    Teleport({
        v, t ->
        TeleportBullet(v, t)
    })
}