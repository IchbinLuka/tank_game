package gameobjects.bullets

import three.js.Vector3

class NoGravityBulletObject(
    startVelocity: Vector3,
    startPos: Vector3,
) : BulletObject(startVelocity, startPos) {
    override var acceleration: Vector3 = Vector3()
}