package gameobjects.particles.emitter

import ext.plus
import three.js.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SphereEmitter(
    val position: Vector3,
    private val radius: Double,
    seed: Int
) : ParticleEmitter {

    private val random = Random(seed)

    override fun randomPoint(): Pair<Position, Normal> {
        val alpha = random.nextDouble() * 2 * PI
        val beta = random.nextDouble() * 2 * PI

        val sphereCoord = Vector3(
            x = radius * sin(alpha) * cos(beta),
            y = radius * sin(alpha) * sin(beta),
            z = radius * cos(beta)
        )
        return Pair(
            Position(sphereCoord + this.position),
            Normal(sphereCoord)
        )
    }
}