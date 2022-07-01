package gameobjects

import core.GameObject
import ext.plus
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
        mainBone = gltf?.scene?.children?.firstOrNull { it.name == "Main" }
        cannon = mainBone?.children?.firstOrNull { it.name == "Cannon" }
        Log.d("Loaded tank")
    }

    private fun move(vector3: Vector3, deltaTime: Float = 1f) {
        renderable?.position?.addScaledVector(vector3, deltaTime)
    }

    private fun setDirection(direction: Direction) {
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
        EventManager.keyboardEvents.events.forEach {
            when(it.keyCode) {
                97  -> {
                    setDirection(Direction.Left)
                    move(Vector3(-1, 0, 0), deltaTime)
                }
                100 -> {
                    setDirection(Direction.Right)
                    move(Vector3(1, 0, 0), deltaTime)
                }
                119 -> rotateCannon(1f, deltaTime)
                115 -> rotateCannon(-1f, deltaTime)
            }
        }
    }


}