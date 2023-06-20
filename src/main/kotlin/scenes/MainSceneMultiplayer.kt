package scenes

import PlayerData

class MainSceneMultiplayer(
    startData: GameMessageData.GameStartData,
    private val player: PlayerData
) : MainScene(startData) {
    override fun isProxy(tank: GameMessageData.TankData): Boolean = tank.player == player
}