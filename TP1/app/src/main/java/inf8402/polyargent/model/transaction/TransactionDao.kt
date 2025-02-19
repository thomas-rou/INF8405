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

    @Query("SELECT * FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIncomeTransactionsBDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpenseTransactionsByDateInterval(startDate: String, endDate: String): LiveData<List<Transaction>>

    @Query("""
    SELECT c.categoryName as categoryName, 
           (SUM(t.amount) / grandTotal.totalAmount) * 100 as percentage,
           SUM(t.amount) as totalAmount
           
    FROM transactions t 
    JOIN categories c ON t.categoryId = c.id 
    CROSS JOIN (SELECT SUM(amount) as totalAmount 
                FROM transactions 
                WHERE type = 'EXPENSE' AND date >= :startDate AND date < :endDate) as grandTotal
    WHERE t.type = 'EXPENSE' AND t.date >= :startDate AND t.date < :endDate 
    GROUP BY c.categoryName, grandTotal.totalAmount 
    ORDER BY totalAmount DESC
""")
    fun getExpenseTransactionsByDateIntervalGroupByCategory(startDate: String, endDate: String): LiveData<List<CategoryReport>>

    @Query("""
    SELECT c.categoryName as categoryName, 
           (SUM(t.amount) / grandTotal.totalAmount) * 100 as percentage,
           SUM(t.amount) as totalAmount
           
    FROM transactions t 
    JOIN categories c ON t.categoryId = c.id 
    CROSS JOIN (SELECT SUM(amount) as totalAmount 
                FROM transactions 
                WHERE type = 'INCOME' AND date >= :startDate AND date < :endDate) as grandTotal
    WHERE t.type = 'INCOME' AND t.date AND t.date >= :startDate AND t.date < :endDate 
    GROUP BY c.categoryName, grandTotal.totalAmount 
    ORDER BY totalAmount DESC
""")
    fun getIncomeTransactionsByDateIntervalGroupByCategory(startDate: String, endDate: String): LiveData<List<CategoryReport>>

    @Query("SELECT categoryName FROM categories WHERE id = :categoryId")
    suspend fun getCategoryName(categoryId: Int): String?



}