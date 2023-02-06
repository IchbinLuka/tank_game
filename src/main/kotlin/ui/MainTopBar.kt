package ui

import ext.fromId
import gameobjects.bullets.Bullet
import org.w3c.dom.Element
import scenes.GameState
import scenes.MainScene
import util.GUIHelper
import kotlin.math.roundToInt

class MainTopBar(
    private val scene: MainScene
): UIController() {
    private val healthElement by lazy { Element.fromId("health") }
    private val nameElement by lazy { Element.fromId("player_name") }

    private val leftButton by lazy { Element.fromId("bullet_button_left") }
    private val rightButton by lazy { Element.fromId("bullet_button_right") }

    private val bulletName by lazy { Element.fromId("bullet_name") }
    private val bulletCount by lazy { Element.fromId("bullet_count") }

    override suspend fun initAssets() {
        GUIHelper.loadTopBar("tool_bar")
        initListeners()
        updateUI()
    }

    private fun initListeners() {
        scene.addListener { _, _, new ->
            updateState(new)
        }
        leftButton?.addEventListener("click", {
            scene.currentState.currentTank.previousBullet()
        })
        rightButton?.addEventListener("click", {
            scene.currentState.currentTank.nextBullet()
        })
    }

    private fun updateState(state: GameState) {
        state.currentTank.addListener { _, _, new ->
            updateBulletCount(new)
        }
        updateUI(state)
    }

    private fun updateBulletCount(bullet: Bullet = scene.currentState.currentTank.currentBullet) {
        val tank = scene.currentState.currentTank
        bulletName?.innerHTML = bullet.toString()
        bulletCount?.innerHTML = tank.bullets[bullet]?.toString() ?: "\u221e"
    }

    private fun updateUI(state: GameState = scene.currentState) {
        updateBulletCount()
        healthElement?.innerHTML = "${(state.currentTank.health * 100).roundToInt()}%"
        nameElement?.innerHTML = state.currentTank.name
    }


}