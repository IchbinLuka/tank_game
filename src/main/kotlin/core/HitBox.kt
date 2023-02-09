package core

import core.gameobjects.GameObject
import ext.minus
import ext.plus
import ext.times
import ext.unaryMinus
import three.js.Euler
import three.js.Matrix4
import three.js.Plane
import three.js.Vector3

interface HitBox {

    val vertices: Iterable<Vector3>

    val associatedObject: GameObject

    fun containsPoint(point: Vector3): Boolean

    fun intersects(other: HitBox): Boolean = vertices.any {
        other.containsPoint(it)
    } || other.vertices.any {
        containsPoint(it)
    }

    fun hitTest(other: HitBox) {
        if (intersects(other)) {
            onIntersection(other)
            other.onIntersection(this)
        }
    }

    fun onIntersection(intersectingBox: HitBox) {}

}


abstract class ConvexPolygonHitBox: HitBox {
    abstract val planes: Iterable<Plane>

    override fun containsPoint(point: Vector3): Boolean = planes.all {
        it.normal.dot(point - it.coplanarPoint(Vector3())).toDouble() <= 0.0
    }
}

open class CuboidHitBox(
    val position: Vector3 = Vector3(),
    val size: Vector3 = Vector3(),
    val rotation: Euler = Euler(),
    override val associatedObject: GameObject
): ConvexPolygonHitBox() {
    final override val planes: Iterable<Plane>
    final override val vertices: Iterable<Vector3>

    init {
        val rotMat = Matrix4().makeRotationFromEuler(rotation)

        val halfSize = size * 0.5

        val normals = listOf(
            Vector3(x = 1),
            Vector3(x = -1),
            Vector3(y = 1),
            Vector3(y = -1),
            Vector3(z = 1),
            Vector3(z = -1)
        ).map { it.applyMatrix4(rotMat) }

        vertices = listOf(
            normals[2] * halfSize.y,
            normals[3] * halfSize.y,
            normals[4] * halfSize.z,
            normals[5] * halfSize.z,
            - normals[2] * halfSize.y,
            - normals[3] * halfSize.y,
            - normals[4] * halfSize.z,
            - normals[5] * halfSize.z,
        ).map { it + position + normals[0] }

        planes = normals.map {
            Plane().setFromNormalAndCoplanarPoint(it, position + it.multiply(size) * 0.5)
        }

    }
}

