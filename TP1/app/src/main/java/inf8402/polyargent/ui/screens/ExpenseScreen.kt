package inf8402.polyargent.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import inf8402.polyargent.ui.fragments.DiceWithButtonAndImage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import inf8402.polyargent.R
import inf8402.polyargent.model.expense.Expense

class ExpenseScreen(
    private val onDeleteClick: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseScreen.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
        holder.itemView.setOnLongClickListener {
            onDeleteClick(expense)  // Long press to delete
            true
        }
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val amountTextView: TextView = itemView.findViewById(R.id.textAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.textDate)

        @SuppressLint("SetTextI18n")
        fun bind(expense: Expense) {
            titleTextView.text = expense.title
            amountTextView.text = "$${expense.amount}"
            dateTextView.text = expense.date
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
            return oldItem == newItem
        }
    }
}
