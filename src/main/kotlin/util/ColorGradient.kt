package util

import three.js.Color

val backgroundGradient = ColorGradient(
    listOf(
        Color(0x64DBFF),
        Color(0x64DBFF),
        Color(0xfc5923),
        Color(0xfc5923),
        Color(0x013699),
        Color(0x013699),
        Color(0x013699),
    )
)

class ColorGradient(
    private val colors: List<Color>,
) {
    fun getColor(progress: Double): Color {
        val index = (progress * (colors.size - 1)).toInt()
        val color1 = colors[index]
        val color2 = colors[index + 1]
        val scaledProgress = progress * (colors.size - 1) - index

        return Color(color1).lerp(color2, scaledProgress)
    }
}