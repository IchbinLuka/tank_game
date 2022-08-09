package particles.emitter

import ext.plus
import ext.times
import three.js.Vector3
import kotlin.math.PI
import kotlin.random.Random

class CircleEmitter(
    var position: Vector3,
    var radius: Float,
    override var normal: Vector3
): ParticleEmitter {

    private val baseVector = Vector3(0, -normal.z.toDouble(), -normal.y.toDouble())//.normalize()

    override fun randomPoint(): Vector3 {
        val angle = Random.nextFloat() * 2 * PI
        val radius = Random.nextFloat() * radius

        return position + baseVector.clone().applyAxisAngle(normal, angle) * radius
    }
}