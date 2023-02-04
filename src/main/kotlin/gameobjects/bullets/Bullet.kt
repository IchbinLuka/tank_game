package gameobjects.bullets

import three.js.Vector3

enum class Bullet(
    val createObject: (velocity: Vector3, pos: Vector3) -> BulletObject
) {
    Normal({ v, p ->
        BulletObject(startPos = p, startVelocity = v)
    }),
    NoGravity({ v, p ->
        NoGravityBulletObject(startPos = p, startVelocity = v)
    })

}