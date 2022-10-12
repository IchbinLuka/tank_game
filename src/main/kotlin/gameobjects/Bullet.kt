package gameobjects

import core.gameobjects.MovingGameObject
import ext.set
import org.khronos.webgl.Float32Array
import three.js.*

class Bullet(
    startVelocity: Vector3,
    startPos: Vector3,
    private val ground: Ground
) : MovingGameObject() {
    override val renderable: Object3D
        get() = Group().apply {
            add(bulletMesh)
            val geometry = BufferGeometry()
            geometry.setAttribute("position", BufferAttribute(Float32Array(vertices.toTypedArray()).asDynamic(), 3))
            add(Points(geometry,
                PointsMaterial().apply {
                    color = Color(0xcccccc)
                }
            ))
        }

    override var position: Vector3
        get() = bulletMesh.position
        set(value) {
            bulletMesh.position.set(value)
        }

    private val vertices = ArrayDeque<Float>()

    private val bulletMesh = Mesh(
        SphereGeometry(radius = bulletRadius),
        MeshPhongMaterial().apply {
            color = Color(0x111111)
        }
    ).apply { this@apply.position.set(startPos) }

    override suspend fun initVisual() {}

    override var velocity: Vector3 = startVelocity
    override var acceleration: Vector3 = Vector3(0, g, 0)

    override fun update(deltaTime: Double) {
        super.update(deltaTime)
        /*val length = vertices.lastIndex
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
        }*/
        if (ground.heightAt(position.x.toDouble()) > position.y.toFloat()) {
            alive = false
            ground.destroy(position.x.toDouble(), explosionWidth)
        }
    }

    companion object {
        const val g = -20
        const val bulletRadius = 0.5f
        const val explosionWidth = 15.0
    }
}