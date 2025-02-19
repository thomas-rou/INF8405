package inf8402.polyargent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.TransactionDatabase
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CategoryScreen(
    onAddCategoryClicked: () -> Unit = {},
    categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModel.Factory(
            TransactionDatabase.getDatabase(
                LocalContext.current,
                CoroutineScope(Dispatchers.IO)
            ).categoryDao()
        )
    )
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dépenses", "Revenus")

    val allCategories by categoryViewModel.categories.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredCategories = when (selectedTabIndex) {
            0 -> allCategories.filter { it.type == TransactionType.EXPENSE }
            1 -> allCategories.filter { it.type == TransactionType.INCOME }
            else -> emptyList()
        }

        CategoryList(categories = filteredCategories)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onAddCategoryClicked) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<Category>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Two columns
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories) { category ->
            CategoryItem(category = category)
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    var isSelected by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .clickable { isSelected = !isSelected }
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(category.colorHex))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconFromString(category.icon),
                    contentDescription = category.categoryName,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.categoryName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun getIconFromString(iconName: String): ImageVector {
    return when (iconName) {
        "ic_circle_help" -> ImageVector.vectorResource(id = R.drawable.ic_circle_help)
        "ic_wallet" -> ImageVector.vectorResource(id = R.drawable.ic_wallet)
        "ic_badge_dollar_sign" -> ImageVector.vectorResource(id = R.drawable.ic_badge_dollar_sign)
        "ic_house" -> ImageVector.vectorResource(id = R.drawable.ic_house)
        "ic_shopping_basket" -> ImageVector.vectorResource(id = R.drawable.ic_shopping_basket)
        "ic_dribble" -> ImageVector.vectorResource(id = R.drawable.ic_dribble)
        else -> Icons.Filled.Add // Default icon if not found
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    CategoryScreen()
}