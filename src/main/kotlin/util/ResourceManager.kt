package util

import three.GLTFLoader
import three.js.TextureLoader

object ResourceManager {
    private val gltfLoader = GLTFLoader()
    private val textureLoader = TextureLoader()

    val suzanne get() = gltfLoader.loadAsync("models/suzanne2.gltf")
    val tank get() = gltfLoader.loadAsync("models/tank.glb")

    val sunnySky get() = textureLoader.loadAsync("textures/skies/sky_sunny.jpg")

}