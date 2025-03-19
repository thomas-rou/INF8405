import androidx.room.*

@Dao
interface TrackedDeviceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackedDevice: TrackedDevice)

    @Query("SELECT * FROM tracked_devices")
    suspend fun getAll(): List<TrackedDevice>

    @Query("SELECT * FROM tracked_devices WHERE address = :address")
    suspend fun getByAddress(address: String): TrackedDevice?

    @Delete
    suspend fun delete(trackedDevice: TrackedDevice)
}
