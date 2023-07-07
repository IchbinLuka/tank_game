import util.ScrollableList
import kotlin.test.Test
import kotlin.test.assertEquals

class ScrollableListTest {
    @Test
    fun testScrollable() {
        val list = ScrollableList(listOf(1, 2, 3))
        assertEquals(1, list.current)
        assertEquals(2, list.next())
        assertEquals(3, list.next())
        assertEquals(1, list.next())
        assertEquals(3, list.previous())
        assertEquals(2, list.previous())
        assertEquals(1, list.previous())
    }
}