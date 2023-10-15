@file:JsModule("three/examples/jsm/math/OBB")
@file:JsNonModule

package three.addons.math

import three.js.*

open external class OBB(
    center: Vector3 = definedExternally,
    halfSize: Vector3 = definedExternally,
    rotation: Matrix3 = definedExternally
) {
    open var center: Vector3
    open var halfSize: Vector3
    open var rotation: Matrix3

    open fun applyMatrix4(matrix: Matrix4): OBB
    open fun clampPoint(point: Vector3, clampedPoint: Vector3): Vector3
    open fun clone(): OBB
    open fun containsPoint(point: Vector3): Boolean
    open fun copy(box: OBB): OBB

    @Suppress("CovariantEquals")
    open fun equals(box: OBB): Boolean

    open fun fromBox3(box3: Box3): OBB
    open fun getSize(size: Vector3): Vector3

    open fun intersectsBox3(box3: Box3): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsOBB(obb: OBB, epsilon: Number = definedExternally): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun intersectsRay(ray: Ray, intersectionPoint: Vector3): Vector3?
    open fun set(center: Vector3, halfSize: Vector3, rotation: Matrix3): OBB
}