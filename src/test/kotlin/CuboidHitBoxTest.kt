import core.CuboidHitBox
import core.gameobjects.GameObject
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import three.js.Object3D
import three.js.Vector3

class EmptyGameObject: GameObject() {
    override val renderable: Object3D? = null
}

class CuboidHitBoxTest: StringSpec({
    "containsPoint should return false if and only if the point is not inside the HitBox" {
        val hitBox = CuboidHitBox(associatedObject = EmptyGameObject(), size = Vector3(1, 1, 1))
        hitBox.containsPoint(Vector3(0, 0, 0)) shouldBe true
        hitBox.containsPoint(Vector3(0.5, 0.5, 0.5)) shouldBe true
        hitBox.containsPoint(Vector3(1, 1, 1)) shouldBe false
        hitBox.containsPoint(Vector3(1.5, 1.5, 1.5)) shouldBe false
        hitBox.containsPoint(Vector3(-2, 4, 3)) shouldBe false
    }
})