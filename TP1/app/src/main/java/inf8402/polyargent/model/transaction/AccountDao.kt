package inf8402.polyargent.model.transaction

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Update
    fun update(account: Account)

    @Query("SELECT balance FROM account WHERE id= :id")
    fun getAccountById(id: Int): LiveData<String>
}