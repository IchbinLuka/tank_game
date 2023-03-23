package scenes

import gameobjects.Tank
import ui.GameEndController
import util.GUIHelper

class GameEndScene(
    val winners: Collection<Tank>
): TankPresentingScene(winners.take(3)) {
    override suspend fun loadAssets() {
        super.loadAssets()
        for (tank in winners) { tank.updateFireRotation() }
        GUIHelper.pushReplacement(GameEndController(this))
    }
}