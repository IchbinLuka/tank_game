@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Uniform: IUniform {
    constructor(value: Any)
    constructor(type: String, value: Any)
    open var type: String
    override var value: Any
    open var dynamic: Boolean
    open var onUpdateCallback: Function<*>
    open fun onUpdate(callback: Function<*>): Uniform
}