package core.gameobjects

import three.js.Vector3

abstract class MovingGameObject: GameObject() {
    abstract var velocity: Vector3
    abstract var acceleration: Vector3

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        velocity.addScaledVector(acceleration, deltaTime)
        position.addScaledVector(velocity, deltaTime)
    }
}