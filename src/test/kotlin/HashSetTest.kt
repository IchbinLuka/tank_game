import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

data class TestClass(var a: String,)

class HashSetTest: StringSpec({
    "a" {
        val set = HashSet<TestClass>()
        val a = TestClass("a")
        val b = TestClass("a")
        set.add(a)
        set.add(b)
        set.first().a = "b"
        a.a shouldBe "b"
        b.a shouldBe  "b"
    }
})