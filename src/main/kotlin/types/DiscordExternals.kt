@file:JsModule("@discord/embedded-app-sdk")
@file:JsNonModule

package types

import kotlin.js.Promise

external interface SdkConfiguration {
    val disableConsoleLogOverride: Boolean
}

external class GetChannelArgs {
    val channel_id: String
}

external class VoiceChannel {
    val id: String
    val name: String?
    val position: Number?
    val guild_id: String?
    val topic: String?
    val bitrate: Number?
    val user_limit: Number?
    val type: Int
    val voice_states: Array<dynamic>

    // TODO: add more properties
}

external class DiscordAPIAuth {
    val access_token: String

}

external class Commands {
    fun authorize(arg: dynamic): Promise<dynamic>

    fun authenticate(arg: dynamic): Promise<dynamic>

    fun getChannel(arg: GetChannelArgs): Promise<VoiceChannel>

}

external class DiscordSDK(clientId: String, configuration: SdkConfiguration = definedExternally) {

    val instanceId: String
    val platform: dynamic
    val guildId: String?
    val channelId: String?
    val configuration: SdkConfiguration
    val commands: Commands

    fun close(code: Int, message: String)

    fun subscribe(event: String, listener: (data: dynamic) -> Unit)
    fun unsubscribe(event: String, listener: (data: dynamic) -> Unit)

    fun ready(): Promise<Unit>
}
