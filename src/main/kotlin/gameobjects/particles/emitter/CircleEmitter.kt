package gameobjects.particles.emitter

import ext.plus
import ext.times
import three.js.Vector3
import kotlin.math.PI
import kotlin.random.Random

class CircleEmitter(
    var position: Vector3,
    var radius: Double,
    var normal: Vector3
): ParticleEmitter {

    private val baseVector = Vector3(0, -normal.z, -normal.y)//.normalize()

    override fun randomPoint(): Pair<Position, Normal> {
        val angle = Random.nextFloat() * 2 * PI
        val radius = Random.nextFloat() * radius

        return Pair(
            Position(position + baseVector.clone().applyAxisAngle(normal, angle) * radius),
            Normal(normal)
        )
    }
}