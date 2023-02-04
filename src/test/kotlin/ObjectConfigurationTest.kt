import generated.ObjectConfiguration
import generated.ObjectConfigurationItem
import generated.Pos
import generated.Rot
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ObjectConfigurationTest : StringSpec({
    "ObjectConfiguration should be correctly serializable to JSON" {
        val conf = ObjectConfiguration(
            arrayListOf(
                ObjectConfigurationItem(
                    name = "Hans",
                    pos = Pos(1.0, 1.0, 1.0),
                    rot = Rot(1.0, 1.0, 1.0)
                ),
                ObjectConfigurationItem(
                    name = "Bernd",
                    pos = Pos(1.0, 1.0, 1.0),
                    rot = Rot(1.0, 1.0, 1.0)
                )
            )
        )

        Json.encodeToString(conf) shouldBe "{\"items\":[{\"name\":\"Hans\",\"pos\":{\"x\":1,\"y\":1,\"z\":1},\"rot\":{\"x\":1,\"y\":1,\"z\":1}},{\"name\":\"Bernd\",\"pos\":{\"x\":1,\"y\":1,\"z\":1},\"rot\":{\"x\":1,\"y\":1,\"z\":1}}]}"

    }
})