package inf8402.polyargent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import inf8402.polyargent.ui.screens.DiceRollerApp
import inf8402.polyargent.ui.theme.PolyArgentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PolyArgentTheme {
                DiceRollerApp()
            }
        }
    }
}