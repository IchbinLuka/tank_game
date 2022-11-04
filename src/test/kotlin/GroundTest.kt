import core.HeightMap
import gameobjects.Ground
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import three.js.Plane
import three.js.Ray
import three.js.Vector3
import util.noise.BoundedNoise

class PlaneNoise(
    private val width: Int,
    private val depth: Int,
    private val normal: Vector3
) : BoundedNoise() {
    override fun calculate(): HeightMap {
        val plane = Plane(normal = normal, constant = 0)
        val direction = Vector3(0, 1, 0)
        return Array<DoubleArray>(width) { i ->
            DoubleArray(depth) { j ->
                val intersection = Vector3()
                Ray(Vector3(i, 0, j), direction).intersectPlane(plane, intersection)
                intersection.y
            }
        }
    }
}

class GroundTest : StringSpec({
    "Ground.heightAt with a flat plane as heightmap should always return 0.0" {
        val flat = Ground(data = PlaneNoise(50, 50, Vector3(0, 1, 0)).allValues())
        flat.heightAt(10.0) shouldBe 0.0
        flat.heightAt(30.0) shouldBe 0.0
    }
    "Ground.heightAt with a tilted plane should return the correct height" {
        val plane = PlaneNoise(50, 50, Vector3(-1, 1, 0)).allValues()
        val tilted = Ground(data = plane)
        tilted.heightAt(10.0) shouldBe 10.0
        tilted.heightAt(30.0) shouldBe 30.0
        tilted.heightAt(30.5) shouldBe 30.5

        val tiltedOffset = Ground(data = plane, offset = Vector3(x = -10))
        tiltedOffset.heightAt(10.0) shouldBe 20.0
        tiltedOffset.heightAt(20.0) shouldBe 30.0
    }
})