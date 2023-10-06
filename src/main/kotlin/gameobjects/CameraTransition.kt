package gameobjects

import core.gameobjects.GameObject
import core.gameobjects.SynchronizedInitializer
import three.js.Camera
import three.js.Object3D
import three.js.Vector3
import kotlin.math.min



class CameraTransition(
    private val camera: Camera,
    private val startAimPos: Vector3,
    private val targetAimPos: Vector3,
    private val duration: Double,
): GameObject() {
    override val renderable: Object3D? = null
    private var time = 0.0

    override fun update(delta: Double) {
        super.update(delta)
        time = min(time + delta, duration)
        val progress = time / duration
        camera.lookAt(vector = startAimPos.clone().lerp(targetAimPos, progress))
        if (time == duration) {
            die()
        }
    }
}