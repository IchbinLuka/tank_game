@file:OptIn(ExperimentalSerializationApi::class)

import core.Game
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import org.w3c.dom.url.URLSearchParams
import scenes.DiscordStartScene
import scenes.StartScene
import services.APIHelper
import util.GUIHelper
import util.Log

fun main() {
    val game = Game(
        if (APIHelper.discord.shouldUseDiscord())
            DiscordStartScene()
        else
            StartScene()
    )
    Log.d("Main", window.location.search)
    Log.d("Main", URLSearchParams(window.location.search).get("instance_id"))
    GUIHelper.initListeners() // TODO: this call should be unnecessary
    game.mainLoop()
}