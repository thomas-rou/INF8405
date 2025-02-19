package inf8402.polyargent.ui.screens

import android.content.Intent
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
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inf8402.polyargent.MainActivity
import inf8402.polyargent.R
import inf8402.polyargent.model.PieChartViewModel
import inf8402.polyargent.model.transaction.CategoryReport
import inf8402.polyargent.ui.fragments.AddTransactionFragment
import inf8402.polyargent.viewmodel.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            percentageTextView.text = categoryReport.percentage.roundToInt().toString()
            amountTextView.text = categoryReport.totalAmount.toString()
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
    val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
//    manageSelectedTab(activity)

    reportScreenAdapter = ReportScreen(reportViewModel)
    setupReportScreen()
}

fun MainActivity.setupReportScreen() {
    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this)
    recyclerView.adapter = this.reportScreenAdapter

    // Observe the expenses LiveData
    this.reportViewModel.ExpenseReportOfToday.observe(this as LifecycleOwner) { categoryReportsGot ->
        this.reportScreenAdapter.submitList(categoryReportsGot)
    }

}