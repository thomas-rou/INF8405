package inf8402.polyargent.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import inf8402.polyargent.R
import inf8402.polyargent.model.expense.Expense
import inf8402.polyargent.databinding.ActivityAddExpenseBinding
import inf8402.polyargent.model.expense.ExpenseType
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
        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }
        binding.dateEditText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        binding.btnSave.setOnClickListener {
            saveExpense()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.navigation_menu, menu)
//        return true
//    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
            binding.dateEditText.text = formattedDate
        }, year, month, day)

        datePickerDialog.show()
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

        val expenseType : ExpenseType
        = if(binding.spinnerExpenseType.selectedItem.toString()=="Dépense")
            ExpenseType.EXPENSE
        else
            ExpenseType.INCOME

        val date = binding.dateEditText.text.toString().trim()

        val expense = Expense(title = title, amount = amount, date = date, type = expenseType)
        expenseViewModel.insert(expense)

        Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
        finish() // Close the activity and go back to the main screen
    }
}
