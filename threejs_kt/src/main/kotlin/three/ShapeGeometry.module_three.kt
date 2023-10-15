@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class ShapeGeometry : BufferGeometry {
    constructor(shapes: Shape, curveSegments: Number = definedExternally)
    constructor(shapes: Array<Shape>, curveSegments: Number = definedExternally)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any): ShapeGeometry
    open fun addShape(shape: Shape, options: Any = definedExternally)
}