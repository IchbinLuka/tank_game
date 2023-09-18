package ui.components

import ext.TAG
import ext.div
import ext.fromId
import ext.tryCast
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.get
import three.js.Vector2
import util.Log
import kotlin.math.sqrt

class JoyStickComponent(
    private val maxDistance: Double = 100.0,
    private val onMove: (Vector2) -> Unit
) : UIComponent() {
    override val componentSource: String = "joystick"

    private val joyStickContainer get() = Element.fromId("joy_stick_container")
    private val joyStickThumb get() = Element.fromId("joy_stick_thumb")?.tryCast<HTMLElement>()

    private val center by lazy {
        joyStickContainer?.getBoundingClientRect()?.let {
            Vector2(it.left + it.width / 2, it.top + it.height / 2)
        }
    }

    private var isDown = false

    private var currentPos = Vector2()

    private fun positionUpdate(pos: Vector2) {

        val containerSize = joyStickContainer?.getBoundingClientRect() ?: return
        val thumbSize = joyStickThumb?.getBoundingClientRect() ?: return

        joyStickThumb?.style?.left = "${pos.x + containerSize.width / 2 - thumbSize.width / 2}px"
        joyStickThumb?.style?.top = "${pos.y + containerSize.height / 2 - thumbSize.height / 2}px"

        onMove(pos / maxDistance)
    }


    override fun onMounted() {
        super.onMounted()
        joyStickThumb?.addEventListener("touchstart", {
            isDown = true
        }) ?: Log.e(TAG, "JoyStick thumb not found")

        document.apply {
            addEventListener("touchend", {
                isDown = false
                currentPos = Vector2()
                positionUpdate(currentPos)
            })
            addEventListener("touchmove", touchListener@{
                if (isDown) {
                    val event = it as TouchEvent

                    val touch = event.touches[0] ?: run {
                        Log.e(TAG, "Touch event has no touches")
                        return@touchListener
                    }

                    val x = touch.clientX - center!!.x
                    val y = touch.clientY - center!!.y

                    val distance = sqrt(x * x + y * y)
                    if (distance > maxDistance) {
                        currentPos.x = x / distance * maxDistance
                        currentPos.y = y / distance * maxDistance
                    } else {
                        currentPos.set(x, y)
                    }

                    positionUpdate(currentPos)
                }
            })
        }
    }
}