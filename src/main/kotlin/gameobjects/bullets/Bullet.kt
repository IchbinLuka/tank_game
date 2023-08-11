package gameobjects.bullets

import Bullet
import gameobjects.GameObjectFactory

val Bullet.createObject: GameObjectFactory<BulletObject, BulletObjectArgs>
    get() = when (this) {
        Bullet.Normal -> BulletObject.Companion
        Bullet.NoGravity -> NoGravityBulletObject.Companion
        Bullet.Big -> BigBulletObject.Companion
        Bullet.Teleport -> TeleportBullet.Companion
    }

suspend fun Bullet.Companion.initFactories() {
    for (item in enumValues<Bullet>()) {
        item.createObject.prepareAssets()
    }
}