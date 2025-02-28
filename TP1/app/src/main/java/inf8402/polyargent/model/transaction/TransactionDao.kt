package inf8402.polyargent.model.transaction

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'INCOME' ORDER BY date DESC")
    fun getAllIncomes(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'INCOME' AND date = :date ORDER BY date DESC")
    fun getIncomeTransactionsByDay(date: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' AND date = :date ORDER BY date DESC")
    fun getExpenseTransactionsByDay(date: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'INCOME' AND strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) " +
            "BETWEEN strftime('%Y-%m-%d', substr(:startDate, 7, 4) || '-' || substr(:startDate, 4, 2) || '-' || substr(:startDate, 1, 2)) " +
            "AND strftime('%Y-%m-%d', substr(:endDate, 7, 4) || '-' || substr(:endDate, 4, 2) || '-' || substr(:endDate, 1, 2)) ORDER BY date DESC")
    fun getIncomeTransactionsBDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' AND strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) " +
            "BETWEEN strftime('%Y-%m-%d', substr(:startDate, 7, 4) || '-' || substr(:startDate, 4, 2) || '-' || substr(:startDate, 1, 2)) " +
            "AND strftime('%Y-%m-%d', substr(:endDate, 7, 4) || '-' || substr(:endDate, 4, 2) || '-' || substr(:endDate, 1, 2)) ORDER BY date DESC")
    fun getExpenseTransactionsByDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>>

    @Query("""
    SELECT c.categoryName as categoryName, 
           (SUM(t.amount) / grandTotal.totalAmount) * 100 as percentage,
           SUM(t.amount) as totalAmount,
           c.icon as icon,
           c.colorHex as colorHex
           
    FROM transactions t 
    JOIN categories c ON t.categoryId = c.id 
    CROSS JOIN (SELECT SUM(amount) as totalAmount 
                FROM transactions 
                WHERE type = 'EXPENSE' AND strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) 
BETWEEN :startDate AND :endDate) as grandTotal
    WHERE t.type = 'EXPENSE' AND strftime('%Y-%m-%d', substr(t.date, 7, 4) || '-' || substr(t.date, 4, 2) || '-' || substr(t.date, 1, 2)) 
BETWEEN :startDate AND :endDate 
    GROUP BY c.categoryName, grandTotal.totalAmount 
    ORDER BY totalAmount DESC
""")
    fun getExpenseTransactionsByDateIntervalGroupByCategory(startDate: String, endDate: String): LiveData<List<CategoryReport>>

    @Query("""
    SELECT c.categoryName as categoryName, 
           (SUM(t.amount) / grandTotal.totalAmount) * 100 as percentage,
           SUM(t.amount) as totalAmount,
           c.icon as icon,
           c.colorHex as colorHex
           
    FROM transactions t 
    JOIN categories c ON t.categoryId = c.id 
    CROSS JOIN (SELECT SUM(amount) as totalAmount 
                FROM transactions 
                WHERE type = 'INCOME' AND strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2))
BETWEEN :startDate AND :endDate) as grandTotal
    WHERE t.type = 'INCOME' AND strftime('%Y-%m-%d', substr(t.date, 7, 4) || '-' || substr(t.date, 4, 2) || '-' || substr(t.date, 1, 2))
BETWEEN :startDate AND :endDate 
    GROUP BY c.categoryName, grandTotal.totalAmount 
    ORDER BY totalAmount DESC
""")
    fun getIncomeTransactionsByDateIntervalGroupByCategory(startDate: String, endDate: String): LiveData<List<CategoryReport>>

    @Query("SELECT categoryName FROM categories WHERE id = :categoryId")
    suspend fun getCategoryName(categoryId: Int): String?

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :categoryId")
    suspend fun getTransactionCountForCategory(categoryId: Int): Int

}