package core.gameobjects

import three.js.Vector3

abstract class MovingGameObject: GameObject() {
    abstract var velocity: Vector3
    abstract var acceleration: Vector3

    override fun update(delta: Double) {
        super.update(delta)
        velocity.addScaledVector(acceleration, delta)
        position.addScaledVector(velocity, delta)
    }
}