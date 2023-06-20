package scenes

import GameMessageData
import core.Player
import ext.toVector3
import gameobjects.Tank
import three.js.Euler

class MultiplayerGameScene(
    private val startData: GameMessageData.GameStartData
): GameScene() {
    private val tanks: List<Tank> = startData.state.tanks.map {
        Tank(
            it.pos.toVector3(),
            Euler().setFromVector3(it.rot.toVector3()),
            Player.fromPlayerData(it.player)
        )
    }


}