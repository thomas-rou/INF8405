import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.room.*

@Entity(tableName = "tracked_devices")
data class TrackedDevice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val address: String, // Adresse MAC unique
    val type: Int,
    val bondState: Int,
    val alias: String?,
    val bluetoothClass: Int?,
    val uuids: List<String>?
) {
    companion object {
        @SuppressLint("MissingPermission", "NewApi")
        fun fromBluetoothDevice(device: BluetoothDevice, latitude: Double, longitude: Double): TrackedDevice {
            return TrackedDevice(
                latitude = latitude,
                longitude = longitude,
                name = device.name,
                address = device.address,
                type = device.type,
                bondState = device.bondState,
                alias = device.alias,
                bluetoothClass = device.bluetoothClass?.deviceClass,
                uuids = device.uuids?.map { it.toString() }
            )
        }
    }
}
