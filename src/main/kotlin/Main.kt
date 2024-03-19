@file:OptIn(ExperimentalSerializationApi::class)

import core.Game
import kotlinx.serialization.ExperimentalSerializationApi
import scenes.DiscordStartScene
import scenes.StartScene
import services.APIHelper
import util.GUIHelper

fun main() {
    val game = Game(
        if (APIHelper.discord.shouldUseDiscord())
            DiscordStartScene()
        else
            StartScene()
    )
    GUIHelper.initListeners() // TODO: this call should be unnecessary
    game.mainLoop()
}