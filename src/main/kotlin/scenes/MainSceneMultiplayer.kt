package scenes

import GameMessageData
import PlayerData
import core.Player
import core.multiplayer.MultiplayerSynchronized
import ext.toVector3
import gameobjects.MultiplayerTank
import gameobjects.Tank
import sessions.Topic

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

    override fun onMessage(message: GameMessageData) {
        when (message) {
            is GameMessageData.GameStateData -> {
                // currentTank = findObject(message.currentTurn)
            }
            else -> {}
        }
    }


}