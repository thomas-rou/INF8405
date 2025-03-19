import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TrackedDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val trackedDeviceDao = db.trackedDeviceDao()

    fun insert(trackedDevice: TrackedDevice) {
        viewModelScope.launch {
            trackedDeviceDao.insert(trackedDevice)
        }
    }

    fun getAll(callback: (List<TrackedDevice>) -> Unit) {
        viewModelScope.launch {
            callback(trackedDeviceDao.getAll())
        }
    }

    fun getByAddress(address: String, callback: (TrackedDevice?) -> Unit) {
        viewModelScope.launch {
            callback(trackedDeviceDao.getByAddress(address))
        }
    }

    fun delete(trackedDevice: TrackedDevice) {
        viewModelScope.launch {
            trackedDeviceDao.delete(trackedDevice)
        }
    }
}
