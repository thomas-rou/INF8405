package inf8402.polyargent.viewmodel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.CategoryDao
import inf8402.polyargent.model.transaction.TransactionDao
import inf8402.polyargent.model.transaction.TransactionType
import kotlinx.coroutines.*

class CategoryViewModel(private val categoryDao: CategoryDao, private val transactionDao: TransactionDao) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>?>()
    val categories: MutableLiveData<List<Category>?> get() = _categories
    val errorMessage = MutableLiveData<String?>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _categoryAdded = MutableLiveData<Boolean>()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        uiScope.launch {
            _categories.value = withContext(Dispatchers.IO) {
                categoryDao.getAllCategories().value
            }
        }
    }

    fun insertCategory(category: Category) {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    categoryDao.insert(category)
                }
                loadCategories()
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

    fun deleteCategory(category: Category) {
        uiScope.launch {
            try{
                val transactionCount = transactionDao.getTransactionCountForCategory(category.id)
                if (transactionCount > 0) {
                    errorMessage.postValue("Cette catégorie est utilisée dans des transactions. Impossible de supprimer.")
                } else {
                    categoryDao.delete(category)
                    loadCategories()
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error deleting category: ${e.message}")
                errorMessage.postValue("Une erreur est survenue.")
            }
        }
    }

    fun updateCategory(category: Category) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.update(category)
            }
            loadCategories()
        }
    }


    fun getCategoriesByType(type: TransactionType): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class Factory(private val categoryDao: CategoryDao, private val transactionDao: TransactionDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(categoryDao, transactionDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
