package scenes

import core.Game
import ext.fromId
import gameobjects.Tank
import org.w3c.dom.Element
import util.GUIHelper

class GameEndScene(
    private val winners: Collection<Tank>
): TankPresentingScene(winners) {
    override suspend fun loadAssets() {
        super.loadAssets()
        for (tank in winners) { tank.updateFireRotation() }
        GUIHelper.loadGUI("game_over")
        Element.fromId("winnerName")?.innerHTML = winners.first().name
        initListener()
    }

    private fun initListener() {
        Element.fromId("returnButton")?.addEventListener("click", {
            Game.instance.currentScene = StartScene()
        })
    }
}