import core.Game
import ext.fromId
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@Suppress("unused")
@OptIn(ExperimentalJsExport::class)
@JsName("DevTools")
@JsExport
object DevTools {
    var gameSpeed: Double = 1.0

    fun activateDeveloperCamera() {
        Game.instance.activateDeveloperCamera()
    }

    var showUI: Boolean
        get() = (Element.fromId("uiContainer") as HTMLElement).style.visibility != "hidden"
        set(value) { (Element.fromId("uiContainer") as HTMLElement).style.visibility = if (value) "visible" else "hidden" }

    fun exportConfiguration(): String = Json.encodeToString(Game.instance.currentScene.getConfiguration())

    fun loadConfiguration(json: String) {
        Game.instance.currentScene.applyConfiguration(Json.decodeFromString(json))
    }
}