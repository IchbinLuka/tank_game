package gameobjects.particles.emitter

import ext.plus
import three.js.Vector3
import kotlin.math.sqrt
import kotlin.random.Random

class SphereEmitter(
    val position: Vector3,
    private val radius: Double,
    seed: Int
) : ParticleEmitter {

    private val random = Random(seed)

    override fun randomPoint(): Pair<Position, Normal> {
        val x = random.nextDouble() * 2 - 1
        val y = random.nextDouble() * 2 - 1
        val z = random.nextDouble() * 2 - 1

        // Just in case
        if (x == y && y == z) {
            return Position(position + Vector3(radius, 0, 0)) to Normal(Vector3(1, 0, 0))
        }

        val coefficient = radius * 1.0 / sqrt(x * x + y * y + z * z)

        val pos = Vector3(
            x * coefficient,
            y * coefficient,
            z * coefficient
        )

        return Pair(
            Position(pos + this.position),
            Normal(pos.normalize())
        )
    }
}