package scenes

import gameobjects.Tank
import three.js.Color
import three.js.Vector3
import ui.StartController
import util.GUIHelper

class StartScene: TankPresentingScene(listOf(
    Color(0x338530),
    Color(0xfc522f),
    Color(0xef3434)
).map { Tank(color = it, startPosition = Vector3()) }) {
    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.pushReplacement(StartController())
    }
}