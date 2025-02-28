package inf8402.polyargent.model.transaction

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [Transaction::class, Category::class, Account::class], version = 2)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.categoryDao())
                    populateDBAccount(database.accountDao())
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            categoryDao.insert(Category(categoryName = "Autres", type = TransactionType.EXPENSE, icon = "ic_circle_help", colorHex = "#EEC000"))
            categoryDao.insert(Category(categoryName = "Autres", type = TransactionType.INCOME, icon = "ic_circle_help", colorHex = "#759F85"))
            categoryDao.insert(Category(categoryName = "Salaire", type = TransactionType.INCOME, icon = "ic_wallet", colorHex = "#759F85"))
            categoryDao.insert(Category(categoryName = "Bonus", type = TransactionType.INCOME, icon = "ic_badge_dollar_sign", colorHex = "#759F85"))
            categoryDao.insert(Category(categoryName = "Loyer", type = TransactionType.EXPENSE, icon = "ic_house", colorHex = "#EEC000"))
            categoryDao.insert(Category(categoryName = "Épiceries", type = TransactionType.EXPENSE, icon = "ic_shopping_basket", colorHex = "#EEC000"))
            categoryDao.insert(Category(categoryName = "Loisirs", type = TransactionType.EXPENSE, icon = "ic_dribble", colorHex = "#EEC000"))
        }
        suspend fun populateDBAccount(accountDao: AccountDao) {
            accountDao.insert(Account(id = 1, balance = "0.00 $"))
        }
    }

    suspend fun clearAllCategories(categoryDao: CategoryDao){
        categoryDao.deleteAllCategories()
    }
}

class Converters {
    @androidx.room.TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @androidx.room.TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @androidx.room.TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}