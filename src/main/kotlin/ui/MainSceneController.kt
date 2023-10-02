package ui

import Bullet
import core.Game
import ext.TAG
import ext.addClickListener
import ext.fromId
import ext.tryCast
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import scenes.MainScene
import three.js.Vector2
import util.*
import kotlin.math.roundToInt

class MainSceneController(
    private val scene: MainScene
) : UIController() {
    override val mainGUIName: String = "main_scene_ui"

    private val healthElement get() = Element.fromId("health")
    private val nameElement get() = Element.fromId("player_name")

    private val bulletLeftButton get() = Element.fromId("bullet_button_left")
    private val bulletRightButton get() = Element.fromId("bullet_button_right")

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
    private val helpButton get() = Element.fromId("help_button")?.tryCast<HTMLButtonElement>()

    override suspend fun init() {
        super.init()

        if (PlatformUtil.isMobile) {
            mobileControls?.style?.visibility = "visible"
        }

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
        bulletLeftButton?.addClickListener {
            scene.currentTankNotifier.value.previousBullet()
        }
        bulletRightButton?.addClickListener {
            scene.currentTankNotifier.value.nextBullet()
        }

        helpButton?.addClickListener {
            MainScope().launch { GUIHelper.push(GuideController()) }
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
        bulletCount?.innerHTML = tank.bullets[bullet]?.toString() ?: "\u221e"
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