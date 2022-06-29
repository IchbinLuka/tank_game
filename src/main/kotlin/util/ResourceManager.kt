package util

import three.GLTFLoader

object ResourceManager {
    private val gltfLoader = GLTFLoader()

    val suzanne get() = gltfLoader.loadAsync("models/suzanne2.gltf")
    val tank get() = gltfLoader.loadAsync("models/tank.gltf")

}