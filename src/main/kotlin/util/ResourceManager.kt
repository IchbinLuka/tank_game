package util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asDeferred
import three.GLTF
import three.GLTFLoader
import three.js.ImageLoader
import three.js.Texture
import three.js.TextureLoader

object ResourceManager {
    val gltfLoader = GLTFLoader()
    val textureLoader = TextureLoader()
    val imageLoader = ImageLoader()

    private fun textureAsync(uri: String): Deferred<Texture> {
        return textureLoader.loadAsync(uri).asDeferred()
    }

    private fun gltfAsync(uri: String): Deferred<GLTF> {
        return gltfLoader.loadAsync(uri).asDeferred()
    }

    val suzanne get() = gltfAsync("models/suzanne2.gltf")
    val tank get() = gltfAsync("models/tank.glb")
    val cloud by lazy { gltfAsync("models/cloud.glb") }

    val tree by lazy { gltfAsync("models/tree.glb") }

    val skyBox get() = gltfAsync("models/skybox.glb")

    val sunnySky get() = textureAsync("textures/skies/sky_sunny.jpg")
    val disc get() = textureAsync("textures/sprites/disc.png")
    val box get() = textureAsync("textures/sprites/box.png")

    val testLandscape get() = gltfAsync("models/test_landscape.glb")

    val startLandScape get() = gltfAsync("models/start_background.glb")

    val winnerPodium get() = gltfAsync("models/winner_podium.glb")
}