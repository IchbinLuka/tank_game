package ext

import three.js.Euler
import three.js.Object3D
import three.js.Quaternion

fun Object3D.enableShadows() {
    traverse {
        if (it.isObject3D) {
            it.receiveShadow = true
            it.castShadow = true
        }
    }
}

fun Object3D.getWorldEuler(target: Euler): Euler {
    val quaternion = getWorldQuaternion(Quaternion().setFromEuler(target))
    return Euler().setFromQuaternion(quaternion)
}