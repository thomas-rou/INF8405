package inf8402.polyargent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import inf8402.polyargent.ui.screens.ExpenseScreen
import inf8402.polyargent.databinding.ExpenseBinding
import inf8402.polyargent.ui.screens.setupExpenseScreen
import inf8402.polyargent.viewmodel.ExpenseViewModel

class MainActivity : AppCompatActivity() {

    private val expenseViewModel: ExpenseViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    private lateinit var adapter: ExpenseScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense)
        adapter = ExpenseScreen { expense -> expenseViewModel.delete(expense) }
        setupExpenseScreen(expenseViewModel, adapter, this)
    }
}