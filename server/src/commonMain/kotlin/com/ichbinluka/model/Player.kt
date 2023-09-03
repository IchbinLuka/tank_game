package com.ichbinluka.model

import Bullet
import GameMessageData
import PlayerData
import com.ichbinluka.exceptions.MissingBulletException
import io.ktor.websocket.*

class Player(
    val data: PlayerData,
    val id: Long,
    val session: WebSocketSession
) {
    val alive: Boolean get() = health > 0
    var health: Double = 1.0
    var fuel: Double = 1.0
        private set

    private lateinit var bullets: HashMap<Bullet, Int?>

    var position: GameMessageData.Vec3 = GameMessageData.Vec3(0.0, 0.0, 0.0)
    var rotation: GameMessageData.Vec3 = GameMessageData.Vec3(0.0, 0.0, 0.0)

    fun getBulletCount(bullet: Bullet): Int? = bullets[bullet]


    fun initPlayer(tankData: GameMessageData.TankData) {
        position = tankData.pos
        rotation = tankData.rot
        bullets = tankData.bulletCounts.getCountsMap()
        fuel = tankData.fuel
    }

    /**
     * Decreases the count of the specified bullet in the bullets map.
     * Throws an exception if there are no more bullets of the specified type.
     *
     * @param bullet the bullet to be shot
     * @throws MissingBulletException if there are no bullets of the specified type
     */
    fun shoot(bullet: Bullet) {
        Bullet.Normal.ordinal
        val count = bullets[bullet] ?: return
        if (count <= 0) throw MissingBulletException()
        bullets[bullet] = count - 1
    }

    fun toTankData() = GameMessageData.TankData(
        health = health,
        player = data,
        pos = position,
        rot = rotation,
        bulletCounts = GameMessageData.BulletCounts.fromMap(bullets),
        fuel = fuel
    )

    override operator fun equals(other: Any?): Boolean = other is Player && other.id == id
    override fun hashCode(): Int {
        return id.hashCode()
    }
}