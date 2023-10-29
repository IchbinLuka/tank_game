@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$49` {
    var total: Number
    var inUse: Number
}

external interface `T$50` {
    var bindingsPerObject: Number
    var objects: `T$49`
}

open external class AnimationObjectGroup(vararg args: Any) {
    open var uuid: String
    open var stats: `T$50`
    open var isAnimationObjectGroup: Boolean
    open fun add(vararg args: Any)
    open fun remove(vararg args: Any)
    open fun uncache(vararg args: Any)
}