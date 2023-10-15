@file:JsModule("three/examples/jsm/loaders/GLTFLoader")
@file:JsNonModule

package three.addons.loaders

import org.w3c.xhr.ProgressEvent
import org.w3c.xhr.XMLHttpRequest
import three.js.AnimationClip
import three.js.Camera
import three.js.Group
import three.js.Loader
import kotlin.js.Promise

external class GLTFLoader : Loader {
    fun load(url: String, onLoad: (GLTF) -> Unit, onProgress: (XMLHttpRequest) -> Unit = definedExternally, onError: (dynamic) -> Unit = definedExternally)

    override fun loadAsync(url: String, onProgress: (event: ProgressEvent) -> Unit): Promise<GLTF>

}

external interface GLTF {
    val scene: Group
    val scenes: Array<Group>
    val animations: Array<AnimationClip>
    val asset: Any
    val cameras: Array<Camera>
}