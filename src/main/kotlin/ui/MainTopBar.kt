package ui

import ext.fromId
import gameobjects.bullets.Bullet
import org.w3c.dom.Element
import scenes.MainScene
import kotlin.math.roundToInt

class MainTopBar(
    private val scene: MainScene
): UIController() {
    override val mainGUIName: String = "tool_bar"

    private val healthElement by lazy { Element.fromId("health") }
    private val nameElement by lazy { Element.fromId("player_name") }

    private val leftButton by lazy { Element.fromId("bullet_button_left") }
    private val rightButton by lazy { Element.fromId("bullet_button_right") }

    private val bulletName by lazy { Element.fromId("bullet_name") }
    private val bulletCount by lazy { Element.fromId("bullet_count") }

    private val aimingStrength by lazy { Element.fromId("strength") }

    override suspend fun init() {
        super.init()
        initListeners()
    }

    override fun rebuild() {
        updateState()
    }

    private fun initListeners() {
        scene.currentTankNotifier.addListener {
            updateState()
        }
        scene.aimingStrength.addListener {
            aimingStrength?.innerHTML = "${(it * 100).roundToInt()}%"
        }
        leftButton?.addEventListener("click", {
            scene.currentTankNotifier.value.previousBullet()
        })
        rightButton?.addEventListener("click", {
            scene.currentTankNotifier.value.nextBullet()
        })
    }

    private fun updateState() {
        scene.currentTankNotifier.value.addListener { _, _, new ->
            updateBulletCount(new)
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
        nameElement?.innerHTML = tank.player.name
    }


}