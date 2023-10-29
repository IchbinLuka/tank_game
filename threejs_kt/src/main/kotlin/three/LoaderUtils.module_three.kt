@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.*

open external class LoaderUtils {
    companion object {
        fun decodeText(array: Int8Array): String
        fun decodeText(array: Uint8Array): String
        fun decodeText(array: Uint8ClampedArray): String
        fun decodeText(array: Int16Array): String
        fun decodeText(array: Uint16Array): String
        fun decodeText(array: Int32Array): String
        fun decodeText(array: Uint32Array): String
        fun decodeText(array: Float32Array): String
        fun decodeText(array: Float64Array): String
        fun extractUrlBase(url: String): String
    }
}