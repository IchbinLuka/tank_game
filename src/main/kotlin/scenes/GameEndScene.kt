package scenes

import core.Player
import gameobjects.Tank
import three.js.Vector3
import ui.GameEndController
import util.GUIHelper

class GameEndScene(
    val winners: Collection<Player>
) : TankPresentingScene(winners.take(3).map { Tank(player = it, startPosition = Vector3()) }) {
    override suspend fun loadAssets() {
        super.loadAssets()
        // for (tank in winners) { tank.updateFireRotation() }
        GUIHelper.pushReplacement(GameEndController(this))
    }
}