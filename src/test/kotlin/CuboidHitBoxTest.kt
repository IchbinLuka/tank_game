import core.CuboidHitBox
import core.gameobjects.GameObject
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import three.js.Euler
import three.js.Object3D
import three.js.Vector3
import kotlin.math.PI

class EmptyGameObject: GameObject() {
    override val renderable: Object3D? = null
}

class CuboidHitBoxTest: StringSpec({
    "containsPoint should return false if and only if the point is not inside the HitBox" {
        val hitBox = CuboidHitBox(associatedObject = EmptyGameObject(), size = Vector3(1, 1, 1))
        forAll(
            row(Vector3(0, 0, 0), true),
            row(Vector3(0.5, 0.5, 0.5), true),
            row(Vector3(1, 1, 1), false),
            row(Vector3(1.5, 1.5, 1.5), false),
            row(Vector3(-2, 4, 3), false)
        ) { a, b ->
            hitBox.containsPoint(a) shouldBe b
        }

        val rotatedHitBox = CuboidHitBox(
            associatedObject = EmptyGameObject(),
            size = Vector3(1, 1, 1),
            rotation = Euler(y=.5 * PI)
        )

        forAll(
            row(Vector3(), true),
            row(Vector3(x = 1, z = 1), false),
            row(Vector3(.5, 0, 0), true),
            row(Vector3(10, 10, 10), false)
        ) { a, b ->
            rotatedHitBox.containsPoint(a) shouldBe b
        }

    }
})