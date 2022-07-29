package ext

import three.js.*
import util.Log

fun Object3D.enableShadows() {
    traverse {
        if (it.isObject3D) {
            it.receiveShadow = true
            it.castShadow = true
        }
    }
}

private const val customMaterialPrefix = "CustomMat"
fun Object3D.applyCustomMat(vararg materials: Material) {
    traverse {
        if (it is Mesh<*, *> && (it.material as Material).name.startsWith(customMaterialPrefix)) {
            val index = (it.material as Material).name.substring(customMaterialPrefix.length).toInt()
            it.material = materials[index - 1].asDynamic()
        }
    }
}

fun Object3D.getWorldEuler(target: Euler): Euler {
    val quaternion = getWorldQuaternion(Quaternion().setFromEuler(target))
    return Euler().setFromQuaternion(quaternion)
}