data class BGColor(val name: String) {
    lateinit var hex: String
    init {
        colorToHex()
    }

    private fun colorToHex() {
        return when (name) {
            "Red" -> hex = "#FF0000"
            "Green" -> hex ="#00FF00"
            "Blue" -> hex = "#0000FF"
            else -> {
                hex = "#FFFFFF"
            }
        }
    }
}