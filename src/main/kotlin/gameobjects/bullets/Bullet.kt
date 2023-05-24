package gameobjects.bullets

import gameobjects.GameObjectFactory
import gameobjects.Tank
import three.js.Vector3

typealias BulletObjectFactory = (velocity: Vector3, tank: Tank) -> BulletObject

enum class Bullet(
    val createObject: GameObjectFactory<BulletObject, BulletObjectArgs>
) {
    Normal(BulletObject.Companion),
    NoGravity(NoGravityBulletObject.Companion),
    Big(BigBulletObject.Companion),
    Teleport(TeleportBullet.Companion);

    companion object {
        suspend fun initFactories() {
            for (item in values()) {
                item.createObject.prepareAssets()
            }
        }
    }
}