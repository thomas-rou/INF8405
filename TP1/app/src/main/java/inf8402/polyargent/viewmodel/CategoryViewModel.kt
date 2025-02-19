package inf8402.polyargent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.CategoryDao
import inf8402.polyargent.model.transaction.TransactionType
import kotlinx.coroutines.*

class CategoryViewModel(private val categoryDao: CategoryDao) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    fun addCategory(category: Category) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.insert(category)
            }
            loadCategories()
        }
    }

    fun deleteCategory(category: Category) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.delete(category)
            }
            loadCategories()
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

    class Factory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(categoryDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
