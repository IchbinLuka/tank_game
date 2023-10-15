@file:JsModule("three/examples/jsm/math/MeshSurfaceSampler")
@file:JsNonModule

package three.addons.math

import three.js.Color
import three.js.Mesh
import three.js.Vector3

open external class MeshSurfaceSampler(
    mesh: Mesh<*, *>
) {
    fun build(): MeshSurfaceSampler
    fun sample(
        targetPosition: Vector3,
        targetNormal: Vector3 = definedExternally,
        targetColor: Color = definedExternally,
        targetUV: dynamic = definedExternally
    ): MeshSurfaceSampler

    fun setWeightAttribute(weightAttribute: String): MeshSurfaceSampler

}