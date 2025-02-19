package inf8402.polyargent.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.TransactionType

class CreateCategoryDialogFragment : DialogFragment() {

    interface OnCategoryCreatedListener {
        fun onCategoryCreated(category: Category)
    }

    var listener: OnCategoryCreatedListener? = null

    private var selectedColor: String = "#FFFFFF"
    private var selectedIcon: String = "ic_circle_help"
    private var selectedTransactionType: TransactionType = TransactionType.EXPENSE

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)

        // Get UI references
        val categoryNameEditText = dialogView.findViewById<EditText>(R.id.editTextNewCategoryName)
        val btnSaveCategory = dialogView.findViewById<Button>(R.id.btnSaveCategory)
        val btnCancelCategory = dialogView.findViewById<Button>(R.id.btnCancelCategory)
        val btnSelectColor = dialogView.findViewById<Button>(R.id.btnSelectColor)
        val btnSelectIcon = dialogView.findViewById<Button>(R.id.btnSelectIcon)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.category_type_rdgroup)
        val iconPreview = dialogView.findViewById<ImageView>(R.id.iconPreview)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)

        // Set up spinner
        val categoryTypes = listOf(TransactionType.EXPENSE, TransactionType.INCOME)
        val categoryTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryTypes)
        categoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedTransactionType = when (checkedId) {
                R.id.category_rdbExpense -> TransactionType.EXPENSE
                R.id.category_rdbincome -> TransactionType.INCOME
                else -> TransactionType.EXPENSE
            }
        }

        btnSelectColor.setOnClickListener {
            showColorPickerDialog { color ->
                selectedColor = color
                btnSelectColor.background = android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor(selectedColor))
                colorPreview.setBackgroundColor(android.graphics.Color.parseColor(selectedColor))
            }
        }

        btnSelectIcon.setOnClickListener {
            showIconPickerDialog { icon ->
                selectedIcon = requireContext().resources.getResourceEntryName(icon)
                iconPreview.setImageResource(icon)
            }
        }

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.add_category))

        // Set up button listeners
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
                listener?.onCategoryCreated(newCategory)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Le nom de la catégorie ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelCategory.setOnClickListener { dismiss() }

        return builder.create()
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
        val alertDialog = AlertDialog.Builder(requireContext())
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
                e.printStackTrace()
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
        val dialog = AlertDialog.Builder(requireContext())
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
}