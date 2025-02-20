package inf8402.polyargent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import inf8402.polyargent.model.transaction.CategoryReport
import inf8402.polyargent.model.transaction.TransactionDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val transactionDao = TransactionDatabase.getDatabase(application, viewModelScope).transactionDao()
    private val currentDate = dateFormat.format(Date())
    val ExpenseReportOfToday = transactionDao.getExpenseTransactionsByDateIntervalGroupByCategory(currentDate,currentDate)
    val IncomeReportOfToday = transactionDao.getIncomeTransactionsByDateIntervalGroupByCategory(currentDate,currentDate)

    fun getExpenseReport(startDate: Date, endDate: Date): LiveData<List<CategoryReport>> {
        return transactionDao.getExpenseTransactionsByDateIntervalGroupByCategory(dateFormat.format(startDate), dateFormat.format(endDate))
    }

    fun getIncomeReport(startDate: Date, endDate: Date): LiveData<List<CategoryReport>> {
        return transactionDao.getIncomeTransactionsByDateIntervalGroupByCategory(dateFormat.format(startDate), dateFormat.format(endDate))
    }

}