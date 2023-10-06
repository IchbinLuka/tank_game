import core.HeightMap
import gameobjects.Ground
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import noise.BoundedNoise
import three.js.Plane
import three.js.Ray
import three.js.Vector3

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
        // Game()
        val flat = Ground(data = PlaneNoise(50, 50, Vector3(0, 1, 0)).allValues())
        flat.preloadAssets()
        flat.heightAt(10.0) shouldBe 0.0
        flat.heightAt(30.0) shouldBe 0.0
    }
    val cross = Vector3(0, 0, 1)
    "Ground.heightAt with a tilted plane should return the correct height" {
        val plane = PlaneNoise(50, 50, Vector3(1, 2, 0).cross(cross)).allValues()
        val tilted = Ground(data = plane)
        tilted.preloadAssets()
        forAll(
            row(0.0, 0.0),
            row(10.0, 20.0),
            row(30.0, 60.0),
            row(30.5, 61.0)
        ) { a, b ->
            tilted.heightAt(a) shouldBe b
        }

        val tiltedOffset = Ground(data = plane, offset = Vector3(x = -10))
        tiltedOffset.preloadAssets()
        tiltedOffset.heightAt(15.0) shouldBe 50.0
        tiltedOffset.heightAt(20.0) shouldBe 60.0
    }
})