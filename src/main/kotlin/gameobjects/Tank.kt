package gameobjects

import core.GameObject
import kotlinx.browser.window
import three.GLTF
import three.js.Object3D
import three.js.Vector3
import util.EventManager
import util.ResourceManager
import util.await

class Tank() : GameObject() {
    override val renderable: Object3D? get() = gltf?.scene

    var gltf: GLTF? = null

    override suspend fun initVisual() {
        gltf = ResourceManager.tank.await()
    }

    override fun update(deltaTime: Float) {
        EventManager.keyboardEvents.events.forEach {
            val vector = when(it.keyCode) {
                39 -> Vector3(1, 0, 0)
                41 -> Vector3(0, 0, 1)
                else -> Vector3(0, 0, 0)
            }
            renderable?.position?.addScaledVector(vector, deltaTime)
        }
    }


}