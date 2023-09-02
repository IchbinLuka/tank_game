package scenes

import GameMessageData
import core.Game
import core.Player
import ext.toVector3
import gameobjects.Tank
import three.js.Color

class MainSceneSingleplayer(
    startData: GameMessageData.GameStartData
) : MainScene(startData) {
    override val tanks: List<Tank> = startData.state.tanks.map {
        Tank(
            player = Player.fromTankData(it),
            startPosition = it.pos.toVector3(),
        ).apply {
            addDeathListener {
                deadPlayers.addFirst(player)
                if (currentTank == this) {
                    nextTank()
                }
                if (isGameOver()) {
                    endGame()
                }
            }
        }
    }.toMutableList()

    override fun isProxy(tank: GameMessageData.TankData): Boolean = false

    override fun onImpact() {
        super.onImpact()
        nextTank()
    }


    private fun nextTank() {
        val aliveTanks = tanks.filter { it.active }
        if (aliveTanks.size <= 1) {
            for (tank in aliveTanks) {
                deadPlayers.addFirst(tank.player)
            }
            Game.instance.currentScene = GameEndScene(deadPlayers)
            return
        } else {
            do {
                currentTankIndex = (currentTankIndex + 1) % tanks.size
            } while (!currentTank.active)
        }
        updateAimingStrength()
        currentTankNotifier.value = currentTank
    }

    override val hasTurn: Boolean = true

    companion object {
        val tankColors = listOf(
            "Green" to Color(0x11710F),
            "Orange" to Color(0xfc522f),
            "Red" to Color(0xef3434),
            "Blue" to Color(0x323cfc),
            "Pink" to Color(0xde43f9)
        )
    }
}