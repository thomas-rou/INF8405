package inf8402.polyargent.model;

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTabViewModel() {
    private val baseDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun getDateRangeForTab(position: Int): String {
        val calendar = baseDate.clone() as Calendar
        return when (position) {
            0 -> "Today, ${dateFormat.format(calendar.time)}"
            1 -> getWeekRange(calendar)
            2 -> getMonthRange(calendar)
            3 -> getYearRange(calendar)
            else -> ""
        }
    }

    private fun getWeekRange(calendar: Calendar): String {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = dateFormat.format(calendar.time)
        calendar.add(Calendar.WEEK_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_WEEK, -1)
        val endOfWeek = dateFormat.format(calendar.time)
        return "$startOfWeek - $endOfWeek"
    }

    private fun getMonthRange(calendar: Calendar): String {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = dateFormat.format(calendar.time)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = dateFormat.format(calendar.time)
        return "$startOfMonth - $endOfMonth"
    }

    private fun getYearRange(calendar: Calendar): String {
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        val startOfYear = dateFormat.format(calendar.time)
        calendar.add(Calendar.YEAR, 1)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val endOfYear = dateFormat.format(calendar.time)
        return "$startOfYear - $endOfYear"
    }

    fun extractDates(input: String): List<String> {
        val regex = "\\d{1,2}/\\d{1,2}/\\d{4}".toRegex()
        return regex.findAll(input).map { it.value }.toList()
    }

    fun adjustBaseDate(currentTab : Int, intToAdd: Int) {
        when (currentTab) {
            0 -> baseDate.add(Calendar.DAY_OF_YEAR, intToAdd)
            1 -> baseDate.add(Calendar.WEEK_OF_MONTH, intToAdd)
            2 -> baseDate.add(Calendar.MONTH, intToAdd)
            3 -> baseDate.add(Calendar.YEAR, intToAdd)
        }
    }
}
