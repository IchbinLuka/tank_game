package scenes

import Bullet
import GameMessage
import GameMessageData
import PlayerData
import core.ConnectionManager
import core.Player
import core.multiplayer.MultiplayerSynchronized
import ext.fromVector3
import ext.toVector3
import gameobjects.MultiplayerTank
import gameobjects.Tank
import gameobjects.bullets.BulletObjectArgs
import gameobjects.bullets.createObject
import sessions.Topic
import three.js.Vector3

class MainSceneMultiplayer(
    startData: GameMessageData.GameStartData,
    private val player: PlayerData
) : MainScene(startData), MultiplayerSynchronized {
    override fun isProxy(tank: GameMessageData.TankData): Boolean = tank.player == player
    override val hasTurn: Boolean
        get() = currentTank.player.name == player.name

    override val topic: String = Topic.MAIN_SCENE


    override val tanks: MutableList<Tank> = startData.state.tanks.map {
        MultiplayerTank(
            player = Player.fromPlayerData(it.player),
            startPosition = it.pos.toVector3(),
            proxy = it.player.name == player.name
        ).apply { addDeathListener { deadPlayers.addFirst(player) } }
    }.toMutableList()

    init {
        ConnectionManager.register(this)
    }





    override fun onBulletCreation(type: Bullet, velocity: Vector3) {
        super.onBulletCreation(type, velocity)
        ConnectionManager.broadcast(
            GameMessage.fromGameMessageData(
                GameMessageData.BulletCreationData(
                    type = type,
                    velocity = GameMessageData.Vec3.fromVector3(velocity)
                ),
                Topic.MAIN_SCENE
            )
        )
    }

    override fun onSceneReady() {
        super.onSceneReady()
        currentTankIndex = tanks.indexOf(findObject<Tank>(startData.state.currentTurn))
        currentTankNotifier.value = currentTank
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.GameStateData -> {
                currentTankIndex = tanks.indexOf(findObject<Tank>(message.currentTurn))
                currentTankNotifier.value = currentTank
            }
            is GameMessageData.BulletCreationData -> {
                currentBulletObject = addSynchronized(
                    message.type.createObject,
                    BulletObjectArgs(
                        message.velocity.toVector3(),
                        currentTank,
                        proxy = true,
                        synchronized = true
                    ),
                )
                currentBulletObject?.addImpactListener { onImpact() }
            }
            is GameMessageData.BulletImpact -> {
                currentBulletObject?.die()
                onImpact()
                // ground.destroy(message.pos.x, message.strength)
            }
            else -> {}
        }
    }


}