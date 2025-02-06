package inf8402.polyargent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import inf8402.polyargent.ui.screens.DiceRollerApp
import inf8402.polyargent.ui.theme.PolyArgentTheme

import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import inf8402.polyargent.ui.screens.ExpenseScreen
import inf8402.polyargent.databinding.ExpenseBinding
import inf8402.polyargent.ui.fragments.AddExpenseFragment
import inf8402.polyargent.viewmodel.ExpenseViewModel

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            PolyArgentTheme {
//                DiceRollerApp()
//            }
//        }
//    }
//}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ExpenseBinding
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExpenseScreen { expense -> expenseViewModel.delete(expense) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        expenseViewModel.allExpenses.observe(this) { expenses ->
            adapter.submitList(expenses)
        }

        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseFragment::class.java))
        }
    }
}