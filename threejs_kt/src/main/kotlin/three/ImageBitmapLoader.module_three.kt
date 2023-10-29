@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import org.w3c.dom.ErrorEvent
import org.w3c.dom.ImageBitmap
import org.w3c.xhr.ProgressEvent

open external class ImageBitmapLoader(manager: LoadingManager = definedExternally) : Loader {
    open var options: Any?
    open var isImageBitmapLoader: Boolean
    open fun setOptions(options: Any?): ImageBitmapLoader
    open fun load(url: String, onLoad: (response: ImageBitmap) -> Unit = definedExternally, onProgress: (request: ProgressEvent) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): Any
}