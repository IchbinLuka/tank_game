@file:JsModule("three/examples/jsm/loaders/GLTFLoader")
@file:JsNonModule
package three

import org.w3c.xhr.ProgressEvent
import three.js.Group
import org.w3c.xhr.XMLHttpRequest
import three.js.Loader
import kotlin.js.Promise

external class GLTFLoader : Loader {
    fun load(url: String, onLoad: (GLTF) -> Unit, onProgress: (XMLHttpRequest) -> Unit = definedExternally, onError: (dynamic) -> Unit = definedExternally)

    override fun loadAsync(url: String, onProgress: (event: ProgressEvent) -> Unit): Promise<GLTF>

}

external interface GLTF {
    val scene: Group
}