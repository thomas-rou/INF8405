package inf8402.polyargent.ui.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import inf8402.polyargent.model.expense.Expense
import inf8402.polyargent.databinding.ActivityAddExpenseBinding
import inf8402.polyargent.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val expenseViewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val title = binding.editTextTitle.text.toString().trim()
        val amountText = binding.editTextAmount.text.toString().trim()

        if (title.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val expense = Expense(title = title, amount = amount, date = currentDate)
        expenseViewModel.insert(expense)

        Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
        finish() // Close the activity and go back to the main screen
    }
}
