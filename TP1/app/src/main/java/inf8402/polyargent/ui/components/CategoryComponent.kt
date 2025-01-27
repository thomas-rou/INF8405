package inf8402.polyargent.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import inf8402.polyargent.R
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import inf8402.polyargent.model.Category
import inf8402.polyargent.model.CategoryNameDisplay
import androidx.compose.ui.Alignment

@Preview()
@Composable
fun CategoryComponent(
    category: Category =  Category(
        stringResource(R.string.default_category_name),
        R.drawable.ic_launcher_foreground, R.color.grey),
    displayName : CategoryNameDisplay = CategoryNameDisplay.NONE
)
{
    when(displayName) {
        CategoryNameDisplay.RIGHT -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DrawCategory(category)
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    category.name,
                    fontSize = 30.sp
                )
            }
        };
        CategoryNameDisplay.BOTTOM -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DrawCategory(category)
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    category.name,
                    fontSize = 30.sp
                )
            }
        };
        else -> {
            DrawCategory(category)
        }
        }
}

@Composable
fun DrawCategory(category :Category) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        val circleColor = colorResource(id = category.color)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = circleColor,
                center = Offset(x = size.width / 2, y = size.height / 2),
                radius = size.minDimension / 2
            )
        }

        Image(
            painter = painterResource(category.icon),
            contentDescription = "",
            modifier = Modifier.size(400.dp)
        )
    }
}