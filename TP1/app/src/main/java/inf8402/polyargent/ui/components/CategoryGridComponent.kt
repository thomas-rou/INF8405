package inf8402.polyargent.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import inf8402.polyargent.model.Category
import inf8402.polyargent.model.CategoryNameDisplay
import inf8402.polyargent.R
import androidx.compose.ui.Modifier
import inf8402.polyargent.ui.components.CategoryComponent



@Composable
fun CategoryGridComponent(
    //TODO: magic numbers
    nColumns : Int = 4,
    categories : List<Category>
) {
    LazyHorizontalGrid(
        rows =  GridCells.Fixed(nColumns),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        items(categories) {
            category -> CategoryComponent(category, CategoryNameDisplay.BOTTOM )
        }
    }
}

//TODO: remove preview function
@Preview
@Composable
private fun TempPreviewCategoryGridComponent(){
    val categories = listOf(
        Category("Santé", R.drawable.ic_launcher_foreground, R.color.red),
        Category("Maison", R.drawable.ic_launcher_foreground, R.color.blue),
        Category("Café", R.drawable.ic_launcher_foreground, R.color.orange),
        Category("Éducation", R.drawable.ic_launcher_foreground, R.color.purple),
        Category("Courses", R.drawable.ic_launcher_foreground, R.color.green),
        Category("Restaurant", R.drawable.ic_launcher_foreground, R.color.light_blue),
    )

    CategoryGridComponent(4,categories)
}