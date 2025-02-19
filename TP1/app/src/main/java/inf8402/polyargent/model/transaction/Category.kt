package inf8402.polyargent.model.transaction

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories",
    indices = [Index(value = ["categoryName", "type"], unique = true)])
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryName: String,
    val isDefault: Boolean = false,
    val type: TransactionType,
    val icon: String,
    val colorHex: String
)