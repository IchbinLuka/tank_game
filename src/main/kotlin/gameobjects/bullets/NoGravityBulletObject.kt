package gameobjects.bullets

import gameobjects.Ground
import three.js.Vector3

class NoGravityBulletObject(startVelocity: Vector3, startPos: Vector3, ground: Ground) : BulletObject(startVelocity, startPos,
    ground
) {
    override var acceleration: Vector3
        get() = Vector3()
        set(value) {}
}