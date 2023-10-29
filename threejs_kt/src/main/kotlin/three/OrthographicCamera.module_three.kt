@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class OrthographicCamera(left: Number, right: Number, top: Number, bottom: Number, near: Number = definedExternally, far: Number = definedExternally) : Camera {
    override var type: String /* 'OrthographicCamera' */
    open var isOrthographicCamera: Boolean
    open var zoom: Number
    open var view: `T$48`?
    open var left: Number
    open var right: Number
    open var top: Number
    open var bottom: Number
    open var near: Number
    open var far: Number
    open fun updateProjectionMatrix()
    open fun setViewOffset(fullWidth: Number, fullHeight: Number, offsetX: Number, offsetY: Number, width: Number, height: Number)
    open fun clearViewOffset()
    open fun toJSON(meta: Any = definedExternally): Any
    override fun toJSON(meta: `T$0`): Any
}