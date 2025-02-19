package inf8402.polyargent.ui.screens

import android.content.Intent
import java.util.Calendar
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
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
import java.text.SimpleDateFormat
import java.util.Locale
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
//            val categoryName = withContext(Dispatchers.IO) {
//                reportViewModel.getCategoryName(transaction.categoryId)
//            }
//            transaction.categoryName = categoryName.toString()
//            holder.bind(transaction)
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
    val barChart: BarChart = findViewById(R.id.reportChart)
    setupStackedBarChart(TimeFrequency.WEEKLY, TransactionType.EXPENSE)
//    manageSelectedTab(activity)

    reportScreenAdapter = ReportScreen(reportViewModel)
    setupReportScreen()
}

fun MainActivity.setupStackedBarChart(timeFrequency: TimeFrequency, transactionType: TransactionType) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val barChart: BarChart = findViewById(R.id.reportChart)
    val mockedColors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN)
    val calendar = Calendar.getInstance()
    val entries = mutableListOf<BarEntry>()
    val dateList = mutableListOf<String>()
    val colors = mutableListOf<Int>()
    val reports = mutableListOf<CategoryReport>()

    for (i in 5 downTo 0) {
        val startDate = calendar.time

        when (timeFrequency){
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

        val endDate = calendar.time

        if (transactionType == TransactionType.EXPENSE) {
            reportViewModel.getExpenseReport(startDate, endDate)
                .observe(this as LifecycleOwner) {
                    reports.addAll(it)
                }
        }
        else {
            reportViewModel.getIncomeReport(startDate, endDate)
                .observe(this as LifecycleOwner) {
                    reports.addAll(it)
                }
        }
        dateList.add(dateFormat.format(startDate))
        colors.add(mockedColors[i])

        val values = reports.map { it.totalAmount.toFloat() }.toFloatArray()
        entries.add(BarEntry(i.toFloat(), values))
    }

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
    xAxis.valueFormatter = IndexAxisValueFormatter(dateList)

    val yAxis = barChart.axisLeft
    yAxis.setDrawLabels(false) // Remove vertical axis legend
    yAxis.setDrawGridLines(false)
    yAxis.setDrawAxisLine(false)
    barChart.axisRight.isEnabled = false // Remove right Y axis

}

fun MainActivity.setupReportScreen() {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    val tabLayout: TabLayout = findViewById(R.id.tabTimePeriodReport)
    tabLayout.getTabAt(1)?.select()
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = this.reportScreenAdapter

    // Observe the expenses LiveData
    this.reportViewModel.ExpenseReportOfToday.observe(this as LifecycleOwner) { categoryReportsGot ->
        this.reportScreenAdapter.submitList(categoryReportsGot)
    }

}