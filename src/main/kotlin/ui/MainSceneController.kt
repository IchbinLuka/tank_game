package ui

import Bullet
import core.Game
import ext.*
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.span
import kotlinx.html.style
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import scenes.MainScene
import three.js.Vector2
import ui.views.menuButton
import ui.views.menuElement
import util.*
import kotlin.math.roundToInt

class MainSceneController(
    private val scene: MainScene
) : UIController() {

    override val mainUIBuilder: UIBuilder = {
        div {
            style = "display: flex; height: 100%; flex-direction: column"
            div("topbarMenu unselectable") {
                style = """
                    display: flex;
                    flex-direction: row;
                    justify-content: space-evenly;
                    align-items: center;
                    width: 100%;
                    margin-top: 5px;
                """
                span("menu-element") {
                    span { s("current_player") }
                    span { id = "player_name" }
                }
                menuElement {
                    style = "display: inline"
                    span { s("bullet") }
                    div {
                        style = "display: inline-block"
                        i("arrow-button left") {
                            onClickFunction = { scene.currentTankNotifier.value.previousBullet() }
                        }
                        span {
                            id = "bullet_name"
                        }
                        span {
                            id = "bullet_count"
                        }
                        i("arrow-button right") {
                            onClickFunction = { scene.currentTankNotifier.value.nextBullet() }
                        }
                    }
                }
                span("menu-element") {
                    span {
                        s("health")
                    }
                    span {
                        id = "health"
                    }
                }
                span("menu-element") {
                    span {
                        s("fuel")
                    }
                    span {
                        id = "fuel"
                    }
                }
                div("menu-element") {
                    style = "display: inline-block; width: 150px"
                    id = "strength_container"
                    span {
                        attributes["lang-key"] = "strength"
                        +"""Strength:"""
                    }
                    span {
                        id = "strength"
                        style = "inline-size: 100px"
                        +"""100%"""
                    }
                }
                menuButton {
                    style = "padding-inline: 15px"
                    +"?"
                    onClickFunction = {
                        MainScope().launch { GUIHelper.push(GuideController()) }
                    }
                }
            }
            div {
                id = "mobile_controls"
                style = """
                    visibility: ${if (PlatformUtil.isMobile) "visible" else "hidden"};
                    display: flex;
                    bottom: 10px;
                    width: 100%;
                    position: absolute;
                    justify - content: space-between;
                    user - select: none;
                """
                div {
                    menuButton {
                        id = "button_move_left"
                        style = "margin: 10px"
                        i("arrow-button left")
                    }
                    menuButton {
                        id = "button_move_right"
                        style = "margin: 10px"
                        i("arrow-button right")
                    }
                }
                div {
                    menuButton {
                        id = "shoot_button"
                        style = "margin: 10px"
                        +"""Shoot"""
                    }
                }
            }
        }
    }

    private val healthElement get() = Element.fromId("health")
    private val nameElement get() = Element.fromId("player_name")

    private val bulletName get() = Element.fromId("bullet_name")
    private val bulletCount get() = Element.fromId("bullet_count")

    private val aimingStrength: HTMLElement? get() = Element.fromId("strength")?.tryCast()
    private val aimingStrengthContainer: HTMLElement? get() = Element.fromId("strength_container")?.tryCast()

    private val fuelElement get() = Element.fromId("fuel")


    // Mobile controls
    private val mobileControls get() = Element.fromId("mobile_controls")?.tryCast<HTMLElement>()
    private val moveRightButton get() = Element.fromId("button_move_right")?.tryCast<HTMLButtonElement>()
    private val moveLeftButton get() = Element.fromId("button_move_left")?.tryCast<HTMLButtonElement>()
    private val shootButton get() = Element.fromId("shoot_button")?.tryCast<HTMLButtonElement>()

    override suspend fun init() {
        super.init()
        initListeners()
    }

    override fun rebuild() {
        updateState()
    }

    private lateinit var pointerMove: (Event) -> Unit

    private lateinit var clickListener: (Event) -> Unit

    private fun initListeners() {
        scene.currentTankNotifier.addListener {
            updateState()
        }
        scene.aimingStrength.addListener {
            aimingStrength?.innerHTML = "${(it * 100).roundToInt()}%"
        }

        val cancel = { _: Event ->
            scene.currentTankNotifier.value.moveDirection = null
        }

        // Prevent context menu from opening on mobile when long pressing
        mobileControls?.oncontextmenu = contextMenu@{
            it.preventDefault()
            it.stopPropagation()
            it.stopImmediatePropagation()
            return@contextMenu false
        }

        moveRightButton?.apply {
            addEventListener("touchstart", {
                scene.currentTankNotifier.value.moveDirection = Direction.Right
            }, objectOf {
                passive = true
            })
            addEventListener("touchend", cancel)
        }

        moveLeftButton?.apply {
            addEventListener("touchstart",
                {
                    scene.currentTankNotifier.value.moveDirection = Direction.Left
                },
                options = objectOf {
                    passive = true
                }
            )
            addEventListener("touchend", cancel)
        }

        shootButton?.addClickListener {
            scene.performShot()
        }

        pointerMove = pointerMove@{
            val (clientX, clientY) = when (it) {
                is MouseEvent -> it.clientX to it.clientY
                is TouchEvent -> {
                    val touch = it.touches[0]
                    touch!!.clientX to touch.clientY
                }
                else -> {
                    Log.e(TAG, "Unknown event type: ${it.type}")
                    return@pointerMove
                }
            }
            val aimingPoint = Vector2()
            val containerBox = Game.instance.gameContainer.getBoundingClientRect()
            val x = clientX.toDouble() - containerBox.left
            val y = clientY.toDouble() - containerBox.top
            aimingPoint.x = (x / containerBox.width) * 2 - 1
            aimingPoint.y = -(y / containerBox.height) * 2 + 1

            scene.updateAimingPoint(aimingPoint)
        }
        clickListener = {
            if (!PlatformUtil.isMobile) scene.performShot()
        }
        if (PlatformUtil.isMobile) {
            window.addEventListener("touchmove", pointerMove, objectOf {
                passive = false
            })
        } else {
            window.addEventListener("pointermove", pointerMove)
        }

        Game.instance.gameContainer.addEventListener("click", clickListener)
    }

    private fun updateState() {
        scene.currentTankNotifier.value.apply {
            addListener { _, _, new ->
                updateBulletCount(new)
            }
            player.fuelNotifier.addListener {
                updateUI()
            }
            player.healthNotifier.addListener {
                updateUI()
            }
        }
        updateUI()
    }

    private fun updateBulletCount(bullet: Bullet = scene.currentTankNotifier.value.currentBullet) {
        val tank = scene.currentTankNotifier.value
        bulletName?.innerHTML = bullet.toString()
        bulletCount?.innerHTML = ": ${tank.bullets[bullet]?.toString() ?: "\u221e"}"
    }

    private fun updateUI() {
        updateBulletCount()
        val tank = scene.currentTankNotifier.value
        healthElement?.innerHTML = "${(tank.health * 100).roundToInt()}%"
        fuelElement?.innerHTML = "${(tank.player.fuel * 100).roundToInt()}%"
        nameElement?.innerHTML = tank.player.name
        aimingStrengthContainer?.style?.display = if (scene.hasTurn) "inline" else "none"
    }

    override fun dispose() {
        super.dispose()
        window.removeEventListener("pointermove", pointerMove)
        Game.instance.gameContainer.removeEventListener("click", clickListener)
    }

}