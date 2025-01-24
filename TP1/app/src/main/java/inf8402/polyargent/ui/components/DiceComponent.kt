package inf8402.polyargent.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import inf8402.polyargent.R
import inf8402.polyargent.model.Dice

 var dice: Dice = Dice()
    @Composable
    fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
        var result by remember { mutableIntStateOf(dice.result) }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(dice.getImageResource()),
                contentDescription = result.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                dice.roll()
                result = dice.result
            }) {
                Text(stringResource(R.string.roll))
            }
        }
    }
