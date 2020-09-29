class ColorFactory {
    private val colorStore = ColorStore()
    var colorListener: ((String) -> Unit)? = null

    fun saveColor(newColor: String) {
        colorStore.currentColor = newColorToHex(newColor)
        colorListener?.invoke(newColor)
    }

    private fun newColorToHex(newColor: String): String {
        println("newColor to hex: $newColor")
        return when (newColor) {
            "Red" -> "#FF0000"
            "Green" -> "#00FF00"
            "Blue" -> "#0000FF"
            else -> {
                "#FFFFFF" // White
            }
        }
    }
}

class ColorStore {
    var currentColor: String = "#000000" // Black
}