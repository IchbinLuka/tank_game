package extensions

import three.js.Object3D

fun Object3D.enableShadows() {
    traverse {
        if (it.isObject3D) {
            it.receiveShadow = true
            it.castShadow = true
        }
    }
}