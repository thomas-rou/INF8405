package inf8402.polyargent.model;

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

public class DateTabViewModel {
    fun getDateRangeForTab(position: Int): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        return when (position) {
            0 -> "Today, ${dateFormat.format(calendar.time)}"
            1 -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startOfWeek = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_WEEK, 6)
                val endOfWeek = dateFormat.format(calendar.time)
                "$startOfWeek - $endOfWeek"
            }
            2 -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = dateFormat.format(calendar.time)
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val endOfMonth = dateFormat.format(calendar.time)
                "$startOfMonth - $endOfMonth"
            }
            3 -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val startOfYear = dateFormat.format(calendar.time)
                calendar.add(Calendar.YEAR, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val endOfYear = dateFormat.format(calendar.time)
                "$startOfYear - $endOfYear"
            }
            else -> ""
        }
    }
}
