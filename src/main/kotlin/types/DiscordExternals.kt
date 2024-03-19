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

external class DiscordAuthUser {
    val username: String
    val discriminator: String
    val id: String
    val avatar: String
    val public_flags: Int
    val global_name: String
}

external class DiscordAuthApplication {
    val description: String
    val icon: String?
    val id: String
    val name: String
}
external class DiscordAPIAuth {
    val access_token: String
    val user: DiscordAuthUser
    val scopes: Array<String>
    val expires: String
    val application: DiscordAuthApplication
}

external class UserSettingsLocale {
    val locale: String
}

external class Commands {
    fun authorize(arg: dynamic): Promise<dynamic>

    fun authenticate(arg: dynamic): Promise<DiscordAPIAuth?>

    fun getChannel(arg: GetChannelArgs): Promise<VoiceChannel>

    fun userSettingsGetLocale(): Promise<UserSettingsLocale>

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
