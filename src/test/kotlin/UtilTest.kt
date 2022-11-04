import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import util.Uuid
import util.getLinearGradient

class UtilTest : StringSpec({
    "getLinearGradient should return the value of a linear gradient between start and end with progress" {
        getLinearGradient(1.0, 2.0, 0.5) shouldBe 1.5
        getLinearGradient(1.0, 2.0, 0.0) shouldBe 1.0
        getLinearGradient(1.0, 2.0, 1.0) shouldBe 2.0
    }

    "Uuid.next should always return different values" {
        var previous = Uuid.next()
        repeat(10000) {
            val next = Uuid.next()
            next shouldNotBe previous
            previous = next
        }
    }
})