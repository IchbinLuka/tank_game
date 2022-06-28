@file:JsModule("three/examples/jsm/loaders/GLTFLoader")
@file:JsNonModule
package three

import three.js.Group
import org.w3c.xhr.XMLHttpRequest

external class GLTFLoader {
    fun load(url: String, onLoad: (GLTF) -> Unit, onProgress: (XMLHttpRequest) -> Unit = definedExternally, onError: (dynamic) -> Unit = definedExternally)
}

external interface GLTF {
    val scene: Group
}