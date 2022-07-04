package util

import three.GLTFLoader
import three.js.ImageLoader
import three.js.TextureLoader

object ResourceManager {
    private val gltfLoader = GLTFLoader()
    private val textureLoader = TextureLoader()
    private val imageLoader = ImageLoader()

    val suzanne get() = gltfLoader.loadAsync("models/suzanne2.gltf")
    val tank get() = gltfLoader.loadAsync("models/tank.glb")
    val cloud get() = gltfLoader.loadAsync("models/cloud.glb")

    val sunnySky get() = textureLoader.loadAsync("textures/skies/sky_sunny.jpg")

    val testLandscape get() = gltfLoader.loadAsync("models/test_landscape.glb")
}