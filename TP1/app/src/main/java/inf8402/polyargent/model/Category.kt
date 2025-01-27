package inf8402.polyargent.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes


data class Category(
    val name: String,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int
)

enum class CategoryNameDisplay{
    NONE,
    RIGHT,
    BOTTOM
}