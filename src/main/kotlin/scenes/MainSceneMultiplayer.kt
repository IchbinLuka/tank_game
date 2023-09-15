package scenes

import Bullet
import GameMessage
import GameMessageData
import PlayerData
import core.ConnectionManager
import core.Game
import core.Player
import core.multiplayer.MultiplayerSynchronized
import ext.TAG
import ext.fromVector3
import ext.toVector3
import ext.tryCast
import gameobjects.MultiplayerTank
import gameobjects.bullets.BulletObjectArgs
import gameobjects.bullets.createObject
import sessions.Topic
import three.js.Vector3
import util.Log

class MainSceneMultiplayer(
    startData: GameMessageData.GameStartData,
    private val player: PlayerData
) : MainScene(startData), MultiplayerSynchronized {
    override fun isProxy(tank: GameMessageData.TankData): Boolean = tank.player == player
    override val hasTurn: Boolean
        get() = currentTank.player.name == player.name

    override val topic: String = Topic.MAIN_SCENE


    override val tanks: MutableList<MultiplayerTank> = startData.state.tanks.map {
        val isSelf = it.player.name == player.name
        MultiplayerTank(
            player = Player.fromTankData(it, synchronizeUpdates = isSelf),
            startPosition = it.pos.toVector3(),
            proxy = !isSelf
        ).apply {
            addDeathListener {
                deadPlayers.addFirst(player)
                if (isGameOver()) {
                    endGame()
                }
            }
        }
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
        currentTankIndex = tanks.indexOfFirst { it.player.name == startData.state.currentTurn }
        currentTankNotifier.value = currentTank
    }

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.GameStateData -> {
                currentTankIndex = tanks.indexOfFirst { it.player.name == message.currentTurn }
                currentTankNotifier.value = currentTank
                val tankNames = tanks.associateBy { it.player.name }
                for (tank in message.tanks) {
                    val gameTank = tankNames[tank.player.name]
                    if (gameTank == null) {
                        Log.e(TAG, "Tank ${tank.player.name} not found")
                        continue
                    }
                    gameTank.player.onMessage(tank.bulletCounts)
                    gameTank.tryCast<MultiplayerTank>()?.onMessage(GameMessageData.PlayerHealthUpdate(tank.health))
                }
            }
            is GameMessageData.BulletCreationData -> {
                Log.d(TAG, "Bullet creation: ${message.type}")
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

            is GameMessageData.GameEnd -> {
                val nameTankMap = tanks.associateBy { it.player.name }
                Log.d(TAG, nameTankMap.toString())
                Game.instance.currentScene = GameEndScene(
                    ((0..<tanks.size) zip listOf(
                        message.first,
                        message.second,
                        message.third
                    )).map { (_, name) ->
                        Log.d(TAG, name)
                        nameTankMap[name]!!.player.apply {
                            if (name != message.first) health = 0.0
                        }
                    }
                )
                endGame()
            }
            else -> {}
        }
    }


}