package gameobjects.particles.emitter

import ext.plus
import three.js.Vector3
import kotlin.random.Random

class SquareEmitter(
    private val position: Vector3,
    val normal: Vector3,
    private val length: Int,
    private val width: Int
) : ParticleEmitter {

    override fun randomPoint(): Pair<Position, Normal> {
        val x = Random.nextFloat() * width
        val y = Random.nextFloat() * length

        return Pair(
            Position(
                Vector3(
                    x - 0.5 * width,
                    y - 0.5 * length
                ) + position
            ),
            Normal(normal)
        )
    }

}