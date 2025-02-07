package inf8402.polyargent.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inf8402.polyargent.R
import inf8402.polyargent.model.expense.Expense
import inf8402.polyargent.model.expense.ExpenseType
import inf8402.polyargent.ui.fragments.AddExpenseFragment
import inf8402.polyargent.viewmodel.ExpenseViewModel

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
        private val typeTextView: TextView = itemView.findViewById(R.id.textType)

        @SuppressLint("SetTextI18n")
        fun bind(expense: Expense) {
            titleTextView.text = expense.title
            amountTextView.text = "$${expense.amount}"
            dateTextView.text = expense.date
            typeTextView.text = if (expense.type == ExpenseType.EXPENSE) "Dépense" else "Revenu"
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

 fun AppCompatActivity.onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.navigation_menu, menu)
    return true
}

fun AppCompatActivity.setupExpenseScreen(
    expenseViewModel: ExpenseViewModel,
    adapter: ExpenseScreen,
    context: Context
) {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter

    // Observe the expenses LiveData
    expenseViewModel.allExpenses.observe(context as LifecycleOwner) { expenses ->
        adapter.submitList(expenses)
    }

    // FloatingActionButton click listener
    val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)
    fabAddExpense.setOnClickListener {
        startActivity(Intent(context, AddExpenseFragment::class.java))
    }
}
