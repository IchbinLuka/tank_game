package core.gameobjects

import three.js.Vector3

abstract class MovingGameObject: GameObject() {
    abstract val velocity: Vector3
    abstract val acceleration: Vector3

    override fun update(delta: Double) {
        super.update(delta)
        velocity.addScaledVector(acceleration, delta)
        position.addScaledVector(velocity, delta)
    }
}