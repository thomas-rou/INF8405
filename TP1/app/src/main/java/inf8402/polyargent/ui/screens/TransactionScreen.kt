package inf8402.polyargent.ui.screens

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import inf8402.polyargent.MainActivity
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.DateTabViewModel
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
            transaction.categoryName = categoryName ?: "Catégorie Inconnue"
            holder.bind(transaction)
            holder.itemView.setOnLongClickListener {
                onDeleteClick(transaction)
                true
            }
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val amountTextView: TextView = itemView.findViewById(R.id.textAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.textDate)
        private val categoryTextView: TextView = itemView.findViewById(R.id.textCategory)

        fun bind(transaction: Transaction) {
            titleTextView.text = transaction.title
            amountTextView.text = "$${transaction.amount}"
            dateTextView.text = transaction.date
            categoryTextView.text = transaction.categoryName ?: "Catégorie Inconnue"

            val context = itemView.context
            if (transaction.type == TransactionType.EXPENSE) {
                amountTextView.setTextColor(context.resources.getColor(R.color.red, context.theme))
            } else if (transaction.type == TransactionType.INCOME) {
                amountTextView.setTextColor(context.resources.getColor(R.color.teal_200, context.theme))
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}

fun MainActivity.homePageSetup(activity: MainActivity) {
    setContentView(R.layout.main_page)
    manageSelectedTab(activity)
    manageSelectedDateRange()
    manageBalanceChange()

    adapter = TransactionScreen(
        onDeleteClick = { transaction ->
            transactionViewModel.delete(transaction)
        },
        transactionViewModel = transactionViewModel
    )
    setupTransactionScreen()
}

// Extension function to set up the transaction screen in MainActivity.
fun MainActivity.setupTransactionScreen() {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = this.adapter

    // Observe the expenses LiveData.
    this.transactionViewModel.allExpenses.observe(this as LifecycleOwner) { transactionsGot ->
        this.adapter.submitList(transactionsGot)
    }

    // FloatingActionButton click listener to open AddTransactionActivity.
    val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fabAddTransaction)
    fabAddTransaction.setOnClickListener {
        startActivity(Intent(this, AddTransactionFragment::class.java))
    }
}

fun MainActivity.manageSelectedTab(activity: MainActivity) {
    val transactionTab : TabLayout = findViewById(R.id.tabs)

    currentTransactionTab = 0
    transactionTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    transactionViewModel.allExpenses.observe(activity as LifecycleOwner) { transactionsGot ->
                        adapter.submitList(transactionsGot)
                    }
                    currentTransactionTab = 0
                }

                1 -> {
                    transactionViewModel.allIncomes.observe(activity as LifecycleOwner) { transactionsGot ->
                        adapter.submitList(transactionsGot)
                    }
                    currentTransactionTab = 1
                }
            }
            manageSelectedDateRange()
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}
