package gameobjects.particles

import core.gameobjects.GameObject
import ext.plusAssign
import ext.times
import ext.toDoubleList
import ext.toFloatList
import gameobjects.particles.emitter.ParticleEmitter
import org.khronos.webgl.Float32Array
import three.DoubleSide
import three.LessEqualDepth
import three.js.*
import util.*
import kotlin.js.Date
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random

/**
 * Class for creating particle systems, inspired by blender's particle systems
 */
open class BasicParticleSystem(
    val particleLifetime: Double,
    val particleLifetimeRandomness: Double = 0.0,
    val totalLifetime: Double = Double.POSITIVE_INFINITY,
    val emitter: ParticleEmitter,
    val gravity: Double = -10.0,
    val startVelocity: Double = 0.0,
    val startDirectionRandomness: Double = 0.0, // TODO: Random start direction,
    val velocityRandomness: Double = 0.0,
    startColor: Color = Color(0xffffff),
    endColor: Color = Color(0xffffff),
    val spawnDelta: Double = Double.POSITIVE_INFINITY,
    val spawnBurst: Int = 0,
    val startSize: Double = 1.0,
    val endSize: Double = 1.0,
    val onFinished: () -> Unit = {},
    var material: Material? = null,
): GameObject() {

    private data class Particle(
        var velocity: Vector3,
        var position: Vector3,
        var size: Double,
        var color: Collection<Double>,
        var birthTime: Double
    )

    private val particles = ArrayDeque<Particle>()
    private val startTime: Double = Date.now()

    private val geometry = BufferGeometry()

    private val startColList = startColor.toDoubleList()
    private val colors = startColList zip endColor.toDoubleList()

    private var renderableP: Object3D? = null

    private var lastBirth: Double = 0.0

    // region - PUBLIC -
    override val renderable: Object3D? get() = renderableP

    init {
        addParticles(spawnBurst)
    }

    override suspend fun initVisual() {

        if (material == null) {
            material = ShaderMaterial().apply {
                side = DoubleSide
                Log.d(TAG, "Loading shaders")
                FileHelper.readFile("shaders/customPointsFragment.glsl")?.let {
                    fragmentShader = it
                }
                FileHelper.readFile("shaders/customPointsVertex.glsl")?.let {
                    vertexShader = it
                }
                val texture = ResourceManager.box.await()
                uniforms = object {
                    @property:JsName("color") val color = Uniform(Color(0xffffff))
                    @property:JsName("pointTexture") val pointTexture = Uniform(texture)
                    @property:JsName("alphaTest") val alphaTest = Uniform(0.9)
                }

                uniformsNeedUpdate = true
                transparent = true
                opacity = 0.4
                depthFunc = LessEqualDepth
                // alphaTest = 0.9
            }

        }
        renderableP = Points(geometry, material!!)
    }

    override fun update(deltaTime: Double) {
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
            }
            particle.velocity.addScaledVector(Direction.Down.vector3, gravity * deltaTime)
            particle.size = getLinearGradient(startSize, endSize, lifeProgress)
            particle.position += particle.velocity * deltaTime
            particle.color = colors.map { getLinearGradient(it.first, it.second, lifeProgress) }
        }
        // Add new particles
        val newParticleCount = floor(min((currentTime - lastBirth), particleLifetime * spawnDelta) / spawnDelta).toInt()
        addParticles(newParticleCount, currentTime)
        updateMesh()
    }
    // endregion

    // region - PRIVATE -

    private fun updateMesh() {
        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        val sizes = mutableListOf<Float>()
        for (particle in particles) {
            vertices.addAll(particle.position.toFloatList())
            colors.addAll(particle.color.map { it.toFloat() })
            sizes.add(particle.size.toFloat())
        }
        geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
        geometry.setAttribute("customColor", BufferAttribute(Float32Array(colors.toTypedArray()).asDynamic(), 3))
        geometry.setAttribute("size", BufferAttribute(Float32Array(sizes.toTypedArray()).asDynamic(), 1))
    }

    private fun addParticles(number: Int = 1, time: Double = Date.now()) {
        for (i in 1 .. number) {
            val deltaTime = particleLifetimeRandomness * particleLifetime * (2 * Random.nextFloat() - 1)
            val deltaVelocity = velocityRandomness * startVelocity * (2 * Random.nextFloat() - 1)
            val point = emitter.randomPoint()
            particles.addFirst(
                Particle(
                    velocity = point.second.vector3 * (startVelocity + deltaVelocity),
                    birthTime = time + deltaTime,
                    size = startSize,
                    color = startColList,
                    position = point.first.vector3
                )
            )
        }
        lastBirth = time
    }
    // endregion

    companion object {
        const val TAG = "BasicParticleSystem"
    }

}