package core

import core.gameobjects.GameObject
import ext.minus
import ext.plus
import ext.times
import three.js.*

interface Hittable {
    val hitBox: HitBox
}

typealias IntersectionListener = (Hittable) -> Unit
abstract class HitBox: GameObject() {

    private val intersectionListeners: MutableList<IntersectionListener> = mutableListOf()

    override val renderable: Object3D = Object3D()

    abstract val vertices: Iterable<Vector3>

    abstract val associatedObject: Hittable

    abstract fun containsPoint(point: Vector3): Boolean

    private fun intersects(other: HitBox): Boolean = vertices.any {
        other.containsPoint(it)
    } || other.vertices.any {
        containsPoint(it)
    }

    fun hitTest(other: Hittable) {
        if (intersects(other.hitBox)) {
            onIntersection(other)
            other.hitBox.onIntersection(associatedObject)
        }
    }

    open fun onIntersection(intersectingBox: Hittable) {
        for (listener in intersectionListeners) {
            listener.invoke(intersectingBox)
        }
    }

    fun addIntersectionListener(listener: IntersectionListener) {
        intersectionListeners.add(listener)
    }

}


abstract class ConvexPolygonHitBox: HitBox() {
    abstract val planes: Iterable<Plane>

    override fun containsPoint(point: Vector3): Boolean = planes.all {
        it.normal.dot(point - it.coplanarPoint(Vector3())).toDouble() <= 0.0
    }
}

open class CuboidHitBox(
    open val size: Vector3 = Vector3(),
    override val associatedObject: Hittable
): ConvexPolygonHitBox() {

    var rotation: Euler
        get() = renderable.rotation
        set(value) { renderable.rotation.setFromVector3(value.toVector3()) }

    final override val planes: Iterable<Plane>
        get() = normals.map {
            Plane().setFromNormalAndCoplanarPoint(it, globalPosition + it.multiply(size) * 0.5)
        }

    final override val vertices: Iterable<Vector3>
        get() {
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
            return listOf(
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