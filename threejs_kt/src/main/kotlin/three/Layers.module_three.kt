@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Layers {
    open var mask: Number
    open fun set(channel: Number)
    open fun enable(channel: Number)
    open fun enableAll()
    open fun toggle(channel: Number)
    open fun disable(channel: Number)
    open fun disableAll()
    open fun test(layers: Layers): Boolean
}