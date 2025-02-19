package inf8402.polyargent.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import inf8402.polyargent.R
import inf8402.polyargent.databinding.ActivityAddTransactionBinding
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModels()
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var selectedColor: String = "#FFFFFF"
    private var selectedIcon: String = "ic_circle_help"
    private var selectedTransactionType: TransactionType = TransactionType.EXPENSE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        setupSpinners()
        setupButtons()
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
        transactionViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.categoryName }
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

        val transactionType = if (binding.spinnerTransactionType.selectedItem.toString() == "Dépense")
            TransactionType.EXPENSE else TransactionType.INCOME
        val date = binding.dateEditText.text.toString().trim()

        val transaction = Transaction(
            title = title,
            amount = amount,
            date = date,
            type = transactionType,
            categoryId = selectedCategory.id
        )
        transactionViewModel.insert(transaction)
        Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun createCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)
        val categoryNameEditText = dialogView.findViewById<EditText>(R.id.editTextNewCategoryName)
        val btnSaveCategory = dialogView.findViewById<Button>(R.id.btnSaveCategory)
        val btnCancelCategory = dialogView.findViewById<Button>(R.id.btnCancelCategory)
        val btnSelectColor = dialogView.findViewById<Button>(R.id.btnSelectColor)
        val btnSelectIcon = dialogView.findViewById<Button>(R.id.btnSelectIcon)
        val spinnerCategoryType = dialogView.findViewById<Spinner>(R.id.spinnerCategoryType)
        val iconPreview = dialogView.findViewById<ImageView>(R.id.iconPreview)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)

        val categoryTypes = listOf(TransactionType.EXPENSE, TransactionType.INCOME)
        val categoryTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryTypes)
        categoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoryType.adapter = categoryTypeAdapter
        spinnerCategoryType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTransactionType = if (position == 0) TransactionType.EXPENSE else TransactionType.INCOME
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSelectColor.setOnClickListener {
            showColorPickerDialog { color ->
                selectedColor = color
                btnSelectColor.background = ColorDrawable(android.graphics.Color.parseColor(selectedColor))
                colorPreview.setBackgroundColor(android.graphics.Color.parseColor(selectedColor))
            }
        }

        btnSelectIcon.setOnClickListener {
            showIconPickerDialog { icon ->
                selectedIcon = resources.getResourceEntryName(icon)
                iconPreview.setImageResource(icon)
            }
        }

        builder.setView(dialogView).setTitle(R.string.add_category)
        val dialog = builder.create()

        btnSaveCategory.setOnClickListener {
            val newCategoryName = categoryNameEditText.text.toString().trim()
            if (newCategoryName.isNotEmpty()) {
                val newCategory = Category(
                    categoryName = newCategoryName,
                    isDefault = false,
                    type = selectedTransactionType,
                    icon = selectedIcon,
                    colorHex = selectedColor
                )
                transactionViewModel.insertCategory(newCategory)
                refreshCategorySpinner()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Le nom de la catégorie ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancelCategory.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showColorPickerDialog(onColorSelected: (String) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_color_picker, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewColors)
        val colors = resources.getStringArray(R.array.color_choices).toList()
        gridView.adapter = object : BaseAdapter() {
            override fun getCount(): Int = colors.size
            override fun getItem(position: Int): Any = colors[position]
            override fun getItemId(position: Int): Long = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.grid_item_color, parent, false)
                val colorView = view.findViewById<View>(R.id.colorView)
                colorView.setBackgroundColor(android.graphics.Color.parseColor(colors[position]))
                return view
            }
        }
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        gridView.setOnItemClickListener { _, _, position, _ ->
            onColorSelected(colors[position])
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showIconPickerDialog(onIconSelected: (Int) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_picker, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewIcons)
        val iconNames = resources.getStringArray(R.array.icon_names)
        val iconMap: Map<String, Int> = iconNames.associateWith { iconName ->
            try {
                val field = R.drawable::class.java.getDeclaredField(iconName)
                field.getInt(null)
            } catch (e: Exception) {
                Log.e("IconPicker", "Icon not found: $iconName", e)
                0
            }
        }.filterValues { it != 0 }
        gridView.adapter = object : BaseAdapter() {
            override fun getCount(): Int = iconMap.size
            override fun getItem(position: Int): Any = iconMap.keys.toList()[position]
            override fun getItemId(position: Int): Long = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.grid_item_icon, parent, false)
                val imageView = view.findViewById<ImageView>(R.id.iconImageView)
                val iconName = iconMap.keys.toList()[position]
                val resId = iconMap[iconName] ?: 0
                imageView.setImageResource(resId)
                return view
            }
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Choisir une icone")
            .setView(dialogView)
            .setNegativeButton("Annuler", null)
            .create()
        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedIconResId = iconMap.values.toList()[position]
            onIconSelected(selectedIconResId)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun refreshCategorySpinner() {
        transactionViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.categoryName }
            categoryAdapter.clear()
            categoryAdapter.addAll(categoryNames)
            categoryAdapter.notifyDataSetChanged()
        }
    }
}
