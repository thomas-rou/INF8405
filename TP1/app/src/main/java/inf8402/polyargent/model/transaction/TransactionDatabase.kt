package inf8402.polyargent.model.transaction

import android.content.Context
import androidx.activity.addCallback
import androidx.activity.result.launch
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [Transaction::class, Category::class], version = 2)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

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
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            categoryDao.insert(Category(name = "Autres", isDefault = true))
            categoryDao.insert(Category(name = "Salaire", isDefault = true))
            categoryDao.insert(Category(name = "Bonus", isDefault = true))
            categoryDao.insert(Category(name = "Loyer", isDefault = true))
            categoryDao.insert(Category(name = "Épiceries", isDefault = true))
            categoryDao.insert(Category(name = "Loisirs", isDefault = true))
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