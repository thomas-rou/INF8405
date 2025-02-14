package inf8402.polyargent.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
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
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.ui.fragments.AddTransactionFragment
import inf8402.polyargent.viewmodel.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionScreen(
    private val onDeleteClick: (Transaction) -> Unit,
    private val transactionViewModel: TransactionViewModel
) : ListAdapter<Transaction, TransactionScreen.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
        CoroutineScope(Dispatchers.Main).launch {
            val categoryName = withContext(Dispatchers.IO) {
                transactionViewModel.getCategoryName(transaction.categoryId)
            }
            transaction.categoryName = categoryName.toString()
            holder.bind(transaction)
            holder.itemView.setOnLongClickListener {
                onDeleteClick(transaction)  // Long press to delete
                true
            }
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val amountTextView: TextView = itemView.findViewById(R.id.textAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.textDate)
        //private val typeTextView: TextView = itemView.findViewById(R.id.textType)
        private val categoryTextView: TextView = itemView.findViewById(R.id.textCategory)

        @SuppressLint("SetTextI18n")
        fun bind(transaction: Transaction) {
            titleTextView.text = transaction.title
            amountTextView.text = "$${transaction.amount}"
            dateTextView.text = transaction.date
            //typeTextView.text = if (transaction.type == TransactionType.EXPENSE) "Dépense" else "Revenu"
            categoryTextView.text = transaction.categoryName ?: "Catégorie Inconnue"

            val context = itemView.context
            if (transaction.type == TransactionType.EXPENSE) {
                //typeTextView.setTextColor(context.resources.getColor(R.color.red, context.theme))
                amountTextView.setTextColor(context.resources.getColor(R.color.red, context.theme))
            } else if (transaction.type == TransactionType.INCOME) {
                //typeTextView.setTextColor(context.resources.getColor(R.color.teal_200, context.theme))
                amountTextView.setTextColor(context.resources.getColor(R.color.teal_200, context.theme))
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

fun AppCompatActivity.setupTransactionScreen(
    transactionViewModel: TransactionViewModel,
    adapter: TransactionScreen,
    context: Context
) {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter

    // Observe the expenses LiveData
    transactionViewModel.allTransactions.observe(context as LifecycleOwner) { transactions ->
        adapter.submitList(transactions)
    }

    // FloatingActionButton click listener
    val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fabAddTransaction)
    fabAddTransaction.setOnClickListener {
        startActivity(Intent(context, AddTransactionFragment::class.java))
    }
}
