package inf8402.polyargent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.model.transaction.TransactionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = TransactionDatabase.getDatabase(application, viewModelScope).transactionDao()
    private val categoryDao = TransactionDatabase.getDatabase(application, viewModelScope).categoryDao()

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllExpenses()

    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionDao.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionDao.delete(transaction)
    }
}