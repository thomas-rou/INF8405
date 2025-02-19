package inf8402.polyargent.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.Transaction
import inf8402.polyargent.model.transaction.TransactionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = TransactionDatabase.getDatabase(application, viewModelScope).transactionDao()
    private val categoryDao = TransactionDatabase.getDatabase(application, viewModelScope).categoryDao()

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    val allIncomes: LiveData<List<Transaction>> = transactionDao.getAllIncomes()
    val allExpenses: LiveData<List<Transaction>> = transactionDao.getAllExpenses()
    val errorMessage = MutableLiveData<String?>()
    private val _categoryAdded = MutableLiveData<Boolean>()

    fun getIncomeTransactionsByDay(date: String): LiveData<List<Transaction>> {
        return transactionDao.getIncomeTransactionsByDay(date)
    }

    fun getExpenseTransactionsByDay(date: String): LiveData<List<Transaction>> {
        return transactionDao.getExpenseTransactionsByDay(date)
    }

    fun getIncomeTransactionsBDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>> {
        return transactionDao.getIncomeTransactionsBDateInterval(startDate, endDate)
    }

    fun getExpenseTransactionsByDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>> {
        return transactionDao.getExpenseTransactionsByDateInterval(startDate, endDate)
    }

    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionDao.insert(transaction)
    }

    fun delete(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionDao.delete(transaction)
    }
    fun insertCategory(category: Category) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    categoryDao.insert(category)
                }
                _categoryAdded.postValue(true)
            } catch (e: SQLiteConstraintException) {
                Log.e("CategoryViewModel", "Error adding category: ${e.message}")
                errorMessage.postValue("Une catégorie avec ce nom et ce type existe déjà.")
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error adding category: ${e.message}")
                errorMessage.postValue("Une erreur est survenue.")
            }
        }
    }

    suspend fun getCategoryName(categoryId: Int): String? {
        return transactionDao.getCategoryName(categoryId)
    }
}