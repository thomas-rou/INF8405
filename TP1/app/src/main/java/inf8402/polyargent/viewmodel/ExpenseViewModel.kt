package inf8402.polyargent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import inf8402.polyargent.model.expense.Expense
import inf8402.polyargent.model.expense.ExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    fun insert(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.insert(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        expenseDao.delete(expense)
    }
}