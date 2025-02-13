package inf8402.polyargent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import inf8402.polyargent.ui.screens.TransactionScreen
import inf8402.polyargent.ui.screens.setupTransactionScreen
import inf8402.polyargent.viewmodel.TransactionViewModel

class MainActivity : AppCompatActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                setContentView(R.layout.transaction);
                adapter = TransactionScreen { transaction -> transactionViewModel.delete(transaction) }
                setupTransactionScreen(transactionViewModel, adapter, this)
                return true
            }

            R.id.navigation_category -> {
                setContentView(R.layout.category);
                return true
            }

            R.id.navigation_report -> {
                setContentView(R.layout.repport);
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var adapter: TransactionScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction)
        adapter = TransactionScreen { transaction -> transactionViewModel.delete(transaction) }
        setupTransactionScreen(transactionViewModel, adapter, this)
    }
}