package inf8402.polyargent.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment
import inf8402.polyargent.R

class DeleteCategoryDialogFragment : DialogFragment() {

    interface OnCategoryDeletedListener {
        fun onCategoryDeleted(categoryId: Int)
    }

    var listener: OnCategoryDeletedListener? = null

    companion object {
        private const val ARG_CATEGORY_ID = "arg_category_id"

        fun newInstance(categoryId: Int): DeleteCategoryDialogFragment {
            return DeleteCategoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
                }
            }
        }
    }

    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY_ID, -1)
        }
        if (categoryId == -1) {
            throw IllegalArgumentException("Category ID is required")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_delete_category, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCategory)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteCategory)

        btnCancel.setOnClickListener { dismiss() }
        btnDelete.setOnClickListener {
            listener?.onCategoryDeleted(categoryId)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(getString(R.string.delete_category))
            .create()
    }
}
