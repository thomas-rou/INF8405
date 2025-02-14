package inf8402.polyargent.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.text
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.databinding.ActivityAddTransactionBinding
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.isNotEmpty
import kotlin.text.trim
import android.app.AlertDialog

class AddTransactionFragment : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        setupSpinners()
        setupButtons()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.navigation_menu, menu)
//        return true
//    }

    private fun setupDatePicker() {
        binding.dateEditText.setOnClickListener {
            showDatePicker()
        }
        binding.dateEditText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

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

    private fun setupSpinners() {
        // Populate category spinner
        transactionViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = categoryAdapter
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.addCategoryButton.setOnClickListener {
            createCategoryDialog()
        }
    }

    private fun saveTransaction() {
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

        // Bind to spinner categories and get the selected category
        val selectedCategoryName = binding.categorySpinner.selectedItem.toString()
        val selectedCategory = transactionViewModel.allCategories.value?.find { it.name == selectedCategoryName }
        if (selectedCategory == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionType : TransactionType
        = if(binding.spinnerTransactionType.selectedItem.toString()=="Dépense")
            TransactionType.EXPENSE
        else
            TransactionType.INCOME

        val date = binding.dateEditText.text.toString().trim()

        val transaction = Transaction(title = title, amount = amount, date = date, type = transactionType, categoryId = selectedCategory.id)
        transactionViewModel.insert(transaction)

        Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show()
        finish() // Close the activity and go back to the main screen
    }

    private fun createCategoryDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)
        val categoryNameEditText = dialogView.findViewById<EditText>(R.id.editTextNewCategoryName)

        builder.setView(dialogView)
            .setTitle(R.string.add_category)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val newCategoryName = categoryNameEditText.text.toString().trim()
                if (newCategoryName.isNotEmpty()) {
                    val newCategory = Category(name = newCategoryName)
                    transactionViewModel.insertCategory(newCategory)
                    // Refresh the spinner
                    refreshCategorySpinner()
                } else {
                    Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    private fun refreshCategorySpinner() {
        transactionViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            categoryAdapter.clear()
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()
        }
    }
}
