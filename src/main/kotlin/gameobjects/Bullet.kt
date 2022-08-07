package gameobjects

import core.GameObject
import ext.plus
import org.khronos.webgl.Float32Array
import three.js.*

class Bullet(
    startVelocity: Vector2,
    startPos: Vector2,
    private val ground: Ground
) : GameObject() {
    override val renderable: Object3D get() = Group().apply {
        add(bulletMesh)
        val geometry = BufferGeometry()
        geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
        add(Points(geometry,
            PointsMaterial().apply {
                color = Color(0xcccccc)
            }
        ))
    }

    private val vertices = ArrayDeque<Float>()

    private val bulletMesh = Mesh(
        SphereGeometry(radius = bulletRadius),
        MeshPhongMaterial().apply {
            color = Color(0x111111)
        }
    )

    override suspend fun initVisual() {}

    private var velocity: Vector2 = startVelocity
    private var position: Vector2 = startPos

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        position.addScaledVector(velocity, deltaTime)
        velocity.y += g * deltaTime
        val length = vertices.lastIndex
        if (position.distanceTo(Vector2(vertices[length - 2], vertices[length - 1])).toFloat() > 4) {
            vertices.apply {
                addFirst(0f)
                addFirst(position.y.toFloat())
                addFirst(position.x.toFloat())
            }
        }
        if (vertices.size > 3 * 10) {
            for (i in 1..3) {
                vertices.removeLast()
            }
        }
        if (ground.heightAt(position.x.toDouble()) > position.y.toFloat()) {
            TODO("Exploding not yet implemented")
        }
    }

    companion object {
        const val g = -0.4
        const val bulletRadius = 0.5f
        const val explosionWidth = 5f
    }
}