package inf8402.polyargent.model

import inf8402.polyargent.R

class Dice {
    var result = 1
        private set

    fun roll() {
        result = (1..6).random()
    }

    fun getImageResource(): Int {
        return when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }
}