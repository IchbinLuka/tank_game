package core

import gameobjects.bullets.Bullet
import three.js.Color

class Player(
    val color: Color,
    val name: String,
    startInventory: Map<Bullet, UInt?> = mapOf()
) {
    val inventory: MutableMap<Bullet, Int?> = hashMapOf<Bullet, Int?>(
        Bullet.Normal to null
    )
    var health: Double = 1.0

    init {
        inventory.putAll(startInventory.mapValues { it.value?.toInt() })
    }
}