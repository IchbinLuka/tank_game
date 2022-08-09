package particles.emitter

import three.js.Vector3
import kotlin.random.Random

class SquareEmitter(
    private val position: Vector3,
    override val normal: Vector3,
    private val length: Int,
    private val width: Int
    ) : ParticleEmitter {

    override fun randomPoint(): Vector3 {
        val x = Random.nextFloat() * width
        val y = Random.nextFloat() * length

        return Vector3(
            x - 0.5 * width,
            y - 0.5 * length
        )
    }

}