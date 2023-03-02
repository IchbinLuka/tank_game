package core

import core.gameobjects.GameObject
import ext.minus
import ext.plus
import ext.times
import ext.toFloatList
import org.khronos.webgl.Float32Array
import three.js.*

interface Hittable {
    val hitBox: HitBox
}

typealias IntersectionListener = (Hittable) -> Unit

abstract class HitBox : GameObject() {

    var intersections: HashSet<Hittable> = hashSetOf()
        private set

    private val intersectionListeners: MutableList<IntersectionListener> = mutableListOf()

    override val renderable: Object3D = Object3D()

    abstract val vertices: Iterable<Vector3>

    abstract val associatedObject: Hittable

    private var newIntersections: HashSet<Hittable> = hashSetOf()

    abstract fun containsPoint(point: Vector3): Boolean

    private fun intersects(other: HitBox): Boolean = vertices.any {
        other.containsPoint(it)
    } || other.vertices.any {
        containsPoint(it)
    }

    private fun hitTest(other: Hittable): Boolean = intersects(other.hitBox) || other.hitBox.intersects(this)

    open fun onIntersectionEnter(intersectingBox: Hittable) {
        for (listener in intersectionListeners) {
            listener.invoke(intersectingBox)
        }
    }

    fun addIntersectionListener(listener: IntersectionListener) {
        intersectionListeners.add(listener)
    }

    private fun getIntersections(): Collection<Hittable> = currentScene.findObjects<Hittable>().filter {
        it != associatedObject && hitTest(it)
    }

    override fun preUpdate() {
        super.preUpdate()
        newIntersections = getIntersections().toHashSet()
    }

    override fun update(delta: Double) {
        for (intersection in newIntersections.minus(intersections)) {
            onIntersectionEnter(intersection)
        }

        intersections = newIntersections
    }

}

class EnsembleHitBox(
    private vararg val hitBoxes: HitBox,
    override val associatedObject: Hittable
) : HitBox() {
    override val vertices: Iterable<Vector3>
        get() = hitBoxes.flatMap { it.vertices }

    override fun containsPoint(point: Vector3): Boolean = hitBoxes.any { it.containsPoint(point) }
}


abstract class ConvexPolygonHitBox(private val showVertices: Boolean = false) : HitBox() {

    private val geometry: BufferGeometry = BufferGeometry()

    override val renderable: Object3D = if (!showVertices) {
        Object3D()
    } else Points(material = PointsMaterial().apply { color = Color(0xffffff) }, geometry = geometry)

    abstract val planes: Iterable<Plane>

    override fun update(delta: Double) {
        super.update(delta)
        if (showVertices) {
            geometry.setAttribute("position",
                BufferAttribute(array = Float32Array(
                    vertices.flatMap { renderable.worldToLocal(it).toFloatList() }.toTypedArray()
                ).asDynamic(), itemSize = 3)
            )
        }

    }

    override fun containsPoint(point: Vector3): Boolean = planes.all {
        it.normal.dot(point - it.coplanarPoint(Vector3())).toDouble() <= 0.0
    }
}

open class CuboidHitBox(
    open val size: Vector3 = Vector3(),
    override val associatedObject: Hittable,
    showVertices: Boolean = false
) : ConvexPolygonHitBox(showVertices) {

    var rotation: Euler
        get() = renderable.rotation
        set(value) {
            renderable.rotation.setFromVector3(value.toVector3())
        }

    final override var planes: Iterable<Plane> = listOf()
        private set

    final override var vertices: Iterable<Vector3> = listOf()
        private set

    private fun updateVertices() {
        val halfSize = size * 0.5
        val normals = normals
        val position = globalPosition
        val v2 = listOf(
            normals[2] * halfSize.y,
            normals[3] * halfSize.y,
        )
        val v3 = listOf(
            normals[4] * halfSize.z,
            normals[5] * halfSize.z,
        )
        vertices = listOf(
            normals[0] * halfSize.x,
            normals[1] * halfSize.x
        ).flatMap { n ->
            v2.flatMap { a ->
                v3.map {
                    position + a + n + it
                }
            }
        }
    }

    private fun updatePlanes() {
        planes = normals.map {
            Plane().setFromNormalAndCoplanarPoint(it, globalPosition + Vector3(it.x * size.x, it.y * size.y, it.z * size.z) * 0.5)
        }
    }

    override fun preUpdate() {
        updateVertices()
        updatePlanes()
        super.preUpdate()
    }

    companion object {
        val unRotatedNormals = listOf(
            Vector3(x = 1),
            Vector3(x = -1),
            Vector3(y = 1),
            Vector3(y = -1),
            Vector3(z = 1),
            Vector3(z = -1)
        )
    }

    private val normals: List<Vector3>
        get() = listOf(
            Vector3(x = 1),
            Vector3(x = -1),
            Vector3(y = 1),
            Vector3(y = -1),
            Vector3(z = 1),
            Vector3(z = -1)
        ).map { it.applyMatrix4(Matrix4().makeRotationFromEuler(globalRotation)) }
}