package gameobjects.particles

import core.gameobjects.GameObject
import ext.plusAssign
import ext.times
import ext.toDoubleList
import ext.vector3
import gameobjects.particles.emitter.ParticleEmitter
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import three.DoubleSide
import three.LessEqualDepth
import three.js.*
import util.*
import kotlin.js.Date
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
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
    startSize: Double = 1.0,
    endSize: Double = 1.0,
    val onFinished: () -> Unit = {},
    var material: Material? = null,
) : GameObject() {

    private data class Particle(
        var velocity: Vector3,
        var position: Vector3,
        var birthTime: Double
    )

    private val startSize: Double = adjustSize(startSize)
    private val endSize: Double = adjustSize(endSize)

    private val particles = ArrayDeque<Particle>()
    private val startTime: Double = Date.now()

    private val geometry = BufferGeometry()

    private val startColList = startColor.toDoubleList()
    private val colors = startColList zip endColor.toDoubleList()

    private var _renderable: Object3D? = null

    private var lastBirth: Double = 0.0

    // region - PUBLIC -
    override val renderable: Object3D? get() = _renderable

    private var initialized = false

    init {
        initMesh()
        addParticles(spawnBurst)
    }

    private fun adjustSize(size: Double): Double = size * window.devicePixelRatio

    override suspend fun preloadAssets() {
        super.preloadAssets()
        if (initialized) return
        initialized = true
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
                val texture = ResourceManager.disc.await()
                uniforms = object {
                    @property:JsName("color")
                    val color = Uniform(Color(0xffffff))
                    @property:JsName("pointTexture")
                    val pointTexture = Uniform(texture)
                    @property:JsName("alphaTest")
                    val alphaTest = Uniform(0.9)
                }

                uniformsNeedUpdate = true
                transparent = true
                opacity = 0.4
                depthFunc = LessEqualDepth
                // alphaTest = 0.9
            }

        }
        _renderable = Points(geometry, material!!)//.apply { up = Vector3(z = -1) }
    }

    private fun initMesh() {
        val size = (spawnBurst + ceil(particleLifetime / spawnDelta)).toInt()
        geometry.setAttribute(
            "position", BufferAttribute(
                Float32Array(length = 3 * size).asDynamic(),
                3
            )
        )
        geometry.setAttribute("customColor", BufferAttribute(Float32Array(length = 3 * size).asDynamic(), 3))
        geometry.setAttribute("size", BufferAttribute(Float32Array(length = size).asDynamic(), 1))
    }

    private var previousCount = 0
    override fun update(delta: Double) {
        super.update(delta)
        if (!alive || particles.isEmpty()) return
        val currentTime = Date.now()
        if (currentTime - startTime > totalLifetime) {
            die()
            onFinished()
        }
        // Update each particle
        particles.forEachIndexed { index, particle ->
            val lifeProgress = (currentTime - particle.birthTime) / particleLifetime
            if (lifeProgress > 1) {
                particles.removeAt(index)
            }
            particle.velocity.addScaledVector(Direction.Down.vector3, gravity * delta)
            particle.position += particle.velocity * delta
        }
        // Add new particles
        val newParticleCount = floor(min((currentTime - lastBirth), particleLifetime * spawnDelta) / spawnDelta).toInt()
        addParticles(newParticleCount, currentTime)
        updateMesh()
    }
    // endregion

    // region - PRIVATE -

    private val positions: BufferAttribute by lazy {
        geometry.getAttribute("position")
    }

    private val sizes: BufferAttribute by lazy {
        geometry.getAttribute("size")
    }

    private val colorsBuffer: BufferAttribute by lazy {
        geometry.getAttribute("customColor")
    }

    private fun updateMesh() {
        val nextCount = max(previousCount, particles.size)
        for (i in 0 until nextCount) {
            val particle = particles.getOrNull(i)
            val position = particle?.position ?: Vector3()
            positions.setXYZ(i, position.x, position.y, position.z)
            val currentTime = Date.now()
            if (particle != null) {
                val lifeProgress = (currentTime - particle.birthTime) / particleLifetime
                val color = colors.map { getLinearGradient(it.first, it.second, lifeProgress) }
                colorsBuffer.setXYZ(i, color[0], color[1], color[2])
                sizes.setX(i, getLinearGradient(startSize, endSize, lifeProgress))
            } else {
                sizes.setX(i, 0)
            }

        }
        previousCount = nextCount
        positions.needsUpdate = true
        colorsBuffer.needsUpdate = true
        sizes.needsUpdate = true
    }

    private fun addParticles(number: Int = 1, time: Double = Date.now()) {
        for (i in 1..number) {
            val deltaTime = particleLifetimeRandomness * particleLifetime * (2 * Random.nextFloat() - 1)
            val deltaVelocity = velocityRandomness * startVelocity * (2 * Random.nextFloat() - 1)
            val point = emitter.randomPoint()
            particles.addFirst(
                Particle(
                    velocity = point.second.vector3 * (startVelocity + deltaVelocity),
                    birthTime = time + deltaTime,
                    // size = startSize,
                    // color = startColList,
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