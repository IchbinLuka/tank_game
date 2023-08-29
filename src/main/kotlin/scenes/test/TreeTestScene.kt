package scenes.test

import core.gameobjects.GameObject
import ext.addClickListener
import ext.aspectRatio
import ext.fromId
import gameobjects.Tree
import kotlinx.browser.window
import org.w3c.dom.Element
import scenes.GameScene
import three.js.*

class TreeTestScene : GameScene() {
    override val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 20.0
    }

    private val tree = Tree(Vector3(), "Tree")

    override val initialChildren: Collection<GameObject> = listOf(
        tree
    )

    override val scene = Scene().apply {
        add(AmbientLight(color = Color(0xffffff)))
        background = Color(0x99f0fc)
    }

    override fun onSceneReady() {
        super.onSceneReady()
        Element.fromId("gameContainer")?.addClickListener {
            tree.die()
        }
    }
}