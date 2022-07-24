package particles

import core.GameObject
import ext.plusAssign
import ext.times
import ext.toFloatList
import org.khronos.webgl.Float32Array
import three.js.*
import util.Log
import util.getLinearGradient
import kotlin.js.Date
import kotlin.math.floor
import kotlin.random.Random

/**
 * Class for creating particle systems, inspired by blender's particle systems
 */
class BasicParticleSystem(
    val particleLifetime: Float,
    val particleLifetimeRandomness: Float = 0f,
    val totalLifetime: Double = Double.POSITIVE_INFINITY,
    val emitter: ParticleEmitter,
    val gravity: Float = -10f,
    val startVelocity: Float = 0f,
    val startDirectionRandomness: Float = 0f, // TODO: Random start direction
    val velocityRandomness: Float = 0f,
    val startColor: Color = Color(0xffffff),
    val endColor: Color = Color(0xffffff),
    val spawnDelta: Float = 1f,
    val spawnBurst: Int = 0,
    val startSize: Float = 1f,
    val endSize: Float = 1f,
    val onFinished: () -> Unit = {}
): GameObject() {

    private data class Particle(
        var velocity: Vector3,
        var position: Vector3,
        var size: Float,
        var color: Color,
        var birthTime: Double
        )

    private val particles = ArrayDeque<Particle>()
    var alive: Boolean = true
    private val startTime: Double = Date.now()

    private val geometry = BufferGeometry()

    override val renderable: Object3D by lazy {
        // Lazy initialization to make sure that geometry is defined
        Points(geometry, PointsMaterial().apply {
            vertexColors = true
            sizeAttenuation = true
        })
    }

    init {
        addParticles(spawnBurst)
    }

    private fun updateMesh() {
        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val sizes = mutableListOf<Float>()
        for (particle in particles) {
            vertices.addAll(particle.position.toFloatList())
            colors.addAll(particle.color.toArray().map { it.toFloat() })
            sizes.add(particle.size)
        }
        geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
        geometry.setAttribute("color", BufferAttribute(Float32Array(colors.toTypedArray()).asDynamic(), 3))
        geometry.setAttribute("size", BufferAttribute(Float32Array(colors.toTypedArray()).asDynamic(), 1))
    }

    private fun addParticles(number: Int = 1, time: Double = Date.now()) {
        Log.d((emitter.normal.clone() * startVelocity).toFloatList().toString())
        for (i in 1 .. number) {
            val deltaTime = particleLifetimeRandomness * particleLifetime * (2 * Random.nextFloat() - 1)
            val deltaVelocity = velocityRandomness * startVelocity * (2 * Random.nextFloat() - 1)
            particles.addFirst(Particle(
                velocity = emitter.normal.clone() * (startVelocity + deltaVelocity),
                birthTime = time + deltaTime,
                size = startSize,
                color = startColor,
                position = emitter.randomPoint()
            ))
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!alive || particles.isEmpty()) return
        val currentTime = Date.now()
        if (currentTime - startTime > totalLifetime) {
            alive = false
            onFinished()
        }
        // Update each particle
        particles.forEachIndexed { index, particle ->
            val lifeProgress = (currentTime - particle.birthTime) / particleLifetime
            if (lifeProgress > 1) {
                particles.removeAt(index)
                /*for (i in 0..particles.lastIndex - index) {
                    particles.removeLast()
                }
                return@forEachIndexed*/
            }
            particle.velocity.addScaledVector(emitter.normal, gravity * deltaTime)
            particle.size = getLinearGradient(startSize, endSize, lifeProgress.toFloat())
            particle.position += particle.velocity * deltaTime
            // TODO: Change particle color
        }
        // Add new particles
        val newParticleCount = floor((currentTime - particles.first().birthTime) / spawnDelta).toInt()
        addParticles(newParticleCount, currentTime)
        updateMesh()
    }

}