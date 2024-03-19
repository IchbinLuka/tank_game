package scenes

import core.ConnectionManager
import core.Player
import ext.TAG
import gameobjects.Tank
import services.APIHelper
import three.js.Color
import three.js.Vector3
import ui.SessionViewController
import ui.StartController
import util.GUIHelper
import util.Log

open class StartScene : TankPresentingScene(
    listOf(
    Color(0x338530),
    Color(0xfc522f),
    Color(0xef3434)
).map {
    Tank(
        startPosition = Vector3(),
        player = Player(color = it, name = "player${it.getHexString()}"),
        showLabel = false
    )
}) {
    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.pushReplacement(StartController())
    }
}

class DiscordStartScene : StartScene() {

    private var controller: SessionViewController? = null

    override suspend fun preloadAssets() {
        super.preloadAssets()
        APIHelper.discord.apply {
            setup()
            val player = getPlayer()
            val session = createSession(player)
            Log.d(TAG, "Session created: $session")

            ConnectionManager.connect(session.id, player)
            Log.d(TAG, "Connected to session")

            controller = SessionViewController(
                session, player
            )
        }
    }

    override suspend fun loadAssets() {
        super.loadAssets()
        GUIHelper.pushReplacement(controller!!)
    }
}