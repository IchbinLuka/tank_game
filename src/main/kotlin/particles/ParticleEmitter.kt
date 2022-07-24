package particles

import three.js.Vector3

interface ParticleEmitter {
    val normal: Vector3
    /**
     * Returns a random point inside the emitter shape
     */
    fun randomPoint(): Vector3
}