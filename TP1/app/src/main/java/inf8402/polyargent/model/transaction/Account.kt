package inf8402.polyargent.model.transaction

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")

data class Account (
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    var balance: String,
)
