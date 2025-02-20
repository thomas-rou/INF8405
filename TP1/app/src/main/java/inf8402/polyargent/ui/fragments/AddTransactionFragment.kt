package inf8402.polyargent.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import inf8402.polyargent.databinding.ActivityAddTransactionBinding
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.ui.dialogs.CreateCategoryDialogFragment
import inf8402.polyargent.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var currentTransactionType: TransactionType = TransactionType.EXPENSE // Default type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        setupSpinners()
        setupButtons()
        observeErrorMessage()
    }

    private fun setupDatePicker() {
        binding.dateEditText.setOnClickListener { showDatePicker() }
        binding.dateEditText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val formattedDate = "$day/${month + 1}/$year"
                binding.dateEditText.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupSpinners() {
        // Populate the category spinner.
        setupTransactionTypeSpinner()
        updateCategorySpinner()
    }

    private fun setupTransactionTypeSpinner() {
        val transactionTypes = listOf("Dépense", "Revenu")
        val transactionTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transactionTypes)
        transactionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionType.adapter = transactionTypeAdapter

        binding.spinnerTransactionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                currentTransactionType = if (transactionTypes[position] == "Dépense") {
                    TransactionType.EXPENSE
                } else {
                    TransactionType.INCOME
                }
                updateCategorySpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun updateCategorySpinner() {
        transactionViewModel.allCategories.observe(this) { categories ->
            val filteredCategories = categories.filter {
                when (currentTransactionType) {
                    TransactionType.EXPENSE -> it.type == TransactionType.EXPENSE
                    TransactionType.INCOME -> it.type == TransactionType.INCOME
                }
            }
            val categoryNames = filteredCategories.map { it.categoryName }
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = categoryAdapter
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener { saveTransaction() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.addCategoryButton.setOnClickListener { createCategoryDialog() }
    }

    private fun saveTransaction() {
        val title = binding.editTextTitle.editText?.text.toString().trim()
        val amountText = binding.editTextAmount.editText?.text.toString().trim()

        if (title.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Entrez un montant valide", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCategoryName = binding.categorySpinner.selectedItem.toString()
        val selectedCategory = transactionViewModel.allCategories.value?.find { it.categoryName == selectedCategoryName }
        if (selectedCategory == null) {
            Toast.makeText(this, "Veuillez sélectionner une catégorie", Toast.LENGTH_SHORT).show()
            return
        }

        val date = binding.dateEditText.text.toString().trim()

        val transaction = Transaction(
            title = title,
            amount = amount,
            date = date,
            type = currentTransactionType,
            categoryId = selectedCategory.id
        )
        transactionViewModel.insert(transaction)
        Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun createCategoryDialog() {
        val dialogFragment = CreateCategoryDialogFragment()
        dialogFragment.listener = object : CreateCategoryDialogFragment.OnCategoryCreatedListener {
            override fun onCategoryCreated(category: Category) {
                transactionViewModel.insertCategory(category)
                refreshCategorySpinner()
            }
        }
        dialogFragment.show(supportFragmentManager, "CreateCategoryDialog")
    }

    private fun refreshCategorySpinner() {
        transactionViewModel.allCategories.observe(this) { categories ->
            val filteredCategories = categories.filter {
                when (currentTransactionType) {
                    TransactionType.EXPENSE -> it.type == TransactionType.EXPENSE
                    TransactionType.INCOME -> it.type == TransactionType.INCOME
                }
            }
            val categoryNames = filteredCategories.map { it.categoryName }
            categoryAdapter.clear()
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun observeErrorMessage() {
        transactionViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}