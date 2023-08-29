package gameobjects.particles.emitter

import ext.plus
import three.js.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

class ConeEmitter(
    val position: Vector3,
    val radius: Double,
    val height: Double,
    seed: Int
) : ParticleEmitter {

    private val random = Random(seed)
    override fun randomPoint(): Pair<Position, Normal> {
        val heightPercentage = random.nextDouble().pow(2)
        val height = height * heightPercentage
        val radius = random.nextDouble() * radius * (1 - heightPercentage)
        val angle = random.nextDouble() * 2 * PI
        return Position(
            Vector3(
                x = radius * sin(angle),
                y = height,
                z = radius * cos(angle)
            ) + position
        ) to Normal(Vector3(0, -1, 0))
    }
}