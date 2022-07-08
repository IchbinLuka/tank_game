package gameobjects

import core.GameObject
import ext.plus
import extensions.enableShadows
import extensions.toFloatList
import kotlinx.browser.window
import three.GLTF
import three.js.Euler
import three.js.Object3D
import three.js.Quaternion
import three.js.Vector3
import util.*
import kotlin.math.PI

class Tank() : GameObject() {
    override val renderable: Object3D? get() = gltf?.scene

    private var gltf: GLTF? = null
    private var mainBone: Object3D? = null

    private var direction = Direction.Right

    private var cannon: Object3D? = null

    override suspend fun initVisual() {
        Log.v("Loading tank")
        gltf = ResourceManager.tank.await()
        gltf?.scene?.enableShadows()
        mainBone = gltf?.scene?.children?.firstOrNull { it.name == "Main" }
        mainBone?.position?.y = 0
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        Log.d("Loaded tank")
    }

    fun move(vector3: Vector3, deltaTime: Float = 1f, ground: Ground) {
        mainBone?.position?.addScaledVector(vector3, deltaTime)
        Log.d("Tank position: ${mainBone?.position?.toFloatList()}")
        mainBone?.position?.let {
            val pos = mainBone!!.position.x.toFloat()
            it.y = ground.heightAt(pos)
            mainBone?.rotation?.z = ground.rotationAt(pos)
        }
    }

    fun setDirection(direction: Direction) {
        if (direction != this.direction) {
            mainBone?.rotateX(PI)
        }
        this.direction = direction
    }

    private fun rotateCannon(rotation: Float, deltaTime: Float = 1f) {
        cannon?.rotation?.apply {
            z += (rotation * deltaTime).toDouble()
        }
    }

    override fun update(deltaTime: Float) {
    }


}