package core

import core.gameobjects.GameObject
import ext.euclidicNorm
import ext.minus
import three.js.Euler
import three.js.Vector3
import kotlin.math.pow
import kotlin.math.sqrt

interface HitBox {

    val pos: Vector3

    fun <T> intersectsObject(other: T): Boolean
        where T : GameObject,
              T : HitBox

    fun <T> getIntersections(others: Iterable<T>): Iterable<T>
            where T : GameObject,
                  T : HitBox
            = others.filter { intersectsObject(it) }
}

/*interface RectangleHitBox: HitBox {
    val width: Double
    val height: Double
    val depth: Double

    override fun <T> intersectsObject(other: T): Boolean where T : GameObject, T : HitBox {
        Box3()
        TODO("Not yet implemented")
    }
}*/

class RectangleHitBox(
    val pos: Vector3,
    val size: Vector3,
    val rot: Euler
) {

    private val boundingRadius = sqrt(size.x.pow(2) + size.y.pow(2) + size.z.pow(2))

    fun intersects(other: RectangleHitBox): Boolean {
        if ((other.pos - pos).euclidicNorm() > other.boundingRadius + boundingRadius) {
            return false
        }
        TODO("Implement intersects")
    }
}

