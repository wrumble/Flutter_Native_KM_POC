class ColorFactory {
    private var currentColor: String = "#000000"
    var colorListener: ((String) -> Unit)? = null

    fun saveColor(newColor: String) {
        currentColor = newColorToHex(newColor)
        colorListener?.invoke(currentColor)
    }

    private fun newColorToHex(newColor: String): String {
        return when (newColor) {
            "Red" -> "#FF0000"
            "Green" -> "#00FF00"
            "Blue" -> "#0000FF"
            else -> {
                "#FFFFFF"
            }
        }
    }
}