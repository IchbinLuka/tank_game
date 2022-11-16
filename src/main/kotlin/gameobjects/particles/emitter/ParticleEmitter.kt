package gameobjects.particles.emitter

import three.js.Vector3

value class Position(val vector3: Vector3)
value class Normal(val vector3: Vector3)

interface ParticleEmitter {
    /**
     * Returns a random point inside the emitter shape
     */
    fun randomPoint(): Pair<Position, Normal>
}