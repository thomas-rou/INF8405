package inf8402.polyargent.ui.screens

import java.util.Calendar
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import inf8402.polyargent.MainActivity
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.CategoryReport
import inf8402.polyargent.model.transaction.TimeFrequency
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.viewmodel.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


class ReportScreen(
    private val reportViewModel: ReportViewModel
) : ListAdapter<CategoryReport, ReportScreen.ReportViewHolder>(ReportDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val categoryReport = getItem(position)
        holder.bind(categoryReport)
        CoroutineScope(Dispatchers.Main).launch {
        }
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.reportCategoryName)
        private val percentageTextView: TextView = itemView.findViewById(R.id.reportPercentage)
        private val amountTextView: TextView = itemView.findViewById(R.id.reportTextAmount)

        fun bind(categoryReport: CategoryReport) {
            categoryNameTextView.text = categoryReport.categoryName
            percentageTextView.text = categoryReport.percentage.roundToInt().toString()+"%"
            amountTextView.text = "$"+categoryReport.totalAmount.toString()
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<CategoryReport>() {
        override fun areItemsTheSame(oldItem: CategoryReport, newItem: CategoryReport): Boolean {
            return oldItem.categoryName == newItem.categoryName
        }

        override fun areContentsTheSame(oldItem: CategoryReport, newItem: CategoryReport): Boolean {
            return oldItem.categoryName == newItem.categoryName
        }
    }
}

fun MainActivity.reportPageSetup(activity: MainActivity) {
    setContentView(R.layout.report)
    val tabLayout: TabLayout = findViewById(R.id.timePeriodReportTab)
    tabLayout.getTabAt(1)?.select()
//    setupStackedBarChart(TimeFrequency.WEEKLY, TransactionType.EXPENSE)
    setupStackedBarChart(frequency, type)
    manageSelectedTabInReportView()
}

fun MainActivity.manageSelectedTabInReportView() {
    val timeTab: TabLayout = findViewById(R.id.timePeriodReportTab)
    val transactionTab : TabLayout = findViewById(R.id.reportTabs)

    transactionTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                        type = TransactionType.EXPENSE
                    }

                1 -> {
                        type = TransactionType.INCOME
                }
            }
            setupStackedBarChart(frequency, type)
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })

    timeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    frequency = TimeFrequency.DAILY
                }

                1 -> {
                    frequency = TimeFrequency.WEEKLY
                }
                2 -> {
                    frequency = TimeFrequency.MONTHLY
                }
                3 -> {
                    frequency = TimeFrequency.YEARLY
                }
            }
            setupStackedBarChart(frequency, type)
        }
        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })

}

fun MainActivity.setupStackedBarChart(timeFrequency: TimeFrequency, transactionType: TransactionType) {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("America/Montreal")
    val barChart: BarChart = findViewById(R.id.reportChart)
    val mockedColors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN)
    val calendar = Calendar.getInstance()
    calendar.timeZone = TimeZone.getTimeZone("America/Montreal")
    calendar.time = Date()
    val entries = mutableListOf<BarEntry>()
    val dateList = mutableListOf<String>()
    val colors = mutableListOf<Int>()
    val reportsOfCurrenTime = mutableListOf<CategoryReport>()

    // Using coroutine scope tied to lifecycle
    lifecycleScope.launch(Dispatchers.Main) {
        for (i in 0..5) {
            val endDate = calendar.time

            when (timeFrequency) {
                TimeFrequency.DAILY -> {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }
                TimeFrequency.WEEKLY -> {
                    calendar.add(Calendar.WEEK_OF_YEAR, -1)
                }
                TimeFrequency.MONTHLY -> {
                    calendar.add(Calendar.MONTH, -1)
                }
                TimeFrequency.YEARLY -> {
                    calendar.add(Calendar.YEAR, -1)
                }
            }

            val startDate = calendar.time

            // Fetch the report data before continuing
            val reportData = if(timeFrequency == TimeFrequency.DAILY) {
                fetchReportData(endDate, endDate, transactionType)
            } else {
                fetchReportData(startDate, endDate, transactionType)
            }
            if(i==0) {
                val currentDateTimeTextView: TextView = findViewById(R.id.currentTime)
                currentDateTimeTextView.text = "Date: " + dateFormat.format(endDate)
                reportsOfCurrenTime.addAll(reportData)
            }

            dateList.add(dateFormat.format(endDate))
            colors.add(mockedColors[i])

            val values = reportData.map { it.totalAmount.toFloat() }.toFloatArray()
            entries.add(BarEntry(i.toFloat(), values))
        }

        // Update the chart after the loop is done
        val dataSet = BarDataSet(entries, "")
        dataSet.colors = colors

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.description.isEnabled = false
        dataSet.setDrawValues(false) // Remove value labels
        barChart.legend.isEnabled = false // Remove legend
        barChart.setFitBars(true)
        barChart.invalidate()

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setLabelCount(dateList.count(), true)
        xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (dateList.size - 1).toFloat()

        val yAxis = barChart.axisLeft
//        yAxis.setDrawLabels(false) // Remove vertical axis legend
//        yAxis.setDrawGridLines(false)
//        yAxis.setDrawAxisLine(false)
        barChart.setTouchEnabled(false)
        barChart.axisRight.isEnabled = false // Remove right Y axis

        reportScreenAdapter = ReportScreen(reportViewModel)
        setupReportScreen(reportsOfCurrenTime)
    }
}

// Suspend function to fetch report data
suspend fun MainActivity.fetchReportData(startDate: Date, endDate: Date, transactionType: TransactionType): List<CategoryReport> {
    return suspendCancellableCoroutine { continuation ->
        val liveData = if (transactionType == TransactionType.EXPENSE) {
            reportViewModel.getExpenseReport(startDate, endDate)
        } else {
            reportViewModel.getIncomeReport(startDate, endDate)
        }
        lateinit var observer: Observer<List<CategoryReport>>
        observer = Observer<List<CategoryReport>> { reports ->
            if (reports != null) {
                continuation.resume(reports) {} // Résolution de la coroutine
                liveData.removeObserver(observer)  // Suppression de l'observateur pour éviter les fuites mémoire
            }
        }

        liveData.observeForever(observer)

        // En cas d'annulation de la coroutine, retirer l'observateur
        continuation.invokeOnCancellation {
            liveData.removeObserver(observer)
        }
    }
}


fun MainActivity.setupReportScreen(categoryReports: List<CategoryReport>) {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = this.reportScreenAdapter

    this.reportScreenAdapter.submitList(categoryReports)
}