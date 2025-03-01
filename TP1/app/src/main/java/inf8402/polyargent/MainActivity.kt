package inf8402.polyargent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import inf8402.polyargent.model.transaction.DateTabViewModel
import com.google.android.material.tabs.TabLayout
import inf8402.polyargent.model.transaction.PieChartViewModel
import inf8402.polyargent.model.transaction.TimeFrequency
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.ui.screens.ReportScreen
import inf8402.polyargent.ui.fragments.CategoryFragment
import inf8402.polyargent.ui.screens.TransactionScreen
import inf8402.polyargent.ui.screens.homePageSetup
import inf8402.polyargent.ui.screens.reportPageSetup
import inf8402.polyargent.viewmodel.ReportViewModel
import inf8402.polyargent.viewmodel.TransactionViewModel
import inf8402.polyargent.viewmodel.HomeViewModel
import java.util.TimeZone


class MainActivity() : AppCompatActivity() {
    var currentTransactionTab : Int = 0
    private val dateTab = DateTabViewModel()
    val pieChartView = PieChartViewModel()
    var frequency : TimeFrequency = TimeFrequency.WEEKLY
    var type : TransactionType = TransactionType.EXPENSE
    lateinit var adapter: TransactionScreen
    lateinit var reportScreenAdapter: ReportScreen

    val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    val reportViewModel: ReportViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    val homeViewModel: HomeViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    fun manageBalanceChange() {
        val totalAmount = findViewById<TextView>(R.id.totalAmount)
        val totalAmountModify = findViewById<ImageView>(R.id.totalAmountModify)

        homeViewModel.getTotalAmount().observe(this@MainActivity as LifecycleOwner) { balance ->
            totalAmount.text = balance ?: "0.00 $"
        }

        totalAmountModify.setOnClickListener {
            showEditAmountDialog(totalAmount)
        }
    }

    private fun showEditAmountDialog(totalAmount: TextView) {
        val editText = EditText(this)
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        editText.setText(totalAmount.text.toString().removeSuffix(" $"))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Amount")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                totalAmount.text = "${editText.text} $"
                homeViewModel.setTotalAmount(totalAmount.text as String)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    fun manageSelectedDateRange() {
        val tabLayout: TabLayout = findViewById(R.id.tabTimePeriod)
        val dateRangeText: TextView = findViewById(R.id.date_range_text)
        val prevDate: TextView = findViewById(R.id.previous)
        val nextDate: TextView = findViewById(R.id.next)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                dateRangeText.text = dateTab.getDateRangeForTab(tab?.position ?: 1)
                filterTransactionsByDate(tab?.position, dateRangeText.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                dateRangeText.text = dateTab.getDateRangeForTab(tab?.position ?: 1)
                filterTransactionsByDate(tab?.position, dateRangeText.text.toString())
            }
        })
        tabLayout.getTabAt(1)?.select()
        dateRangeText.text = dateTab.getDateRangeForTab(1)
        filterTransactionsByDate(1, dateRangeText.text.toString())

        prevDate.setOnClickListener {
            dateTab.adjustBaseDate(tabLayout.selectedTabPosition, -1)
            tabLayout.getTabAt(tabLayout.selectedTabPosition)?.select()
        }
        nextDate.setOnClickListener {
            dateTab.adjustBaseDate(tabLayout.selectedTabPosition, 1)
            tabLayout.getTabAt(tabLayout.selectedTabPosition)?.select()
        }
    }

    private fun filterTransactionsByDate(tabPos: Int?, date: String) {
        val pieChart: PieChart = findViewById(R.id.chart)
        pieChart.centerText = "0.0 $"
        val dates = dateTab.extractDates(date)
        if (tabPos == 0) {
            if (currentTransactionTab == 0) {
                transactionViewModel.getExpenseTransactionsByDay(dates[0]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
                homeViewModel.getExpensesTotalAmount(dates[0], dates[0]).observe(this@MainActivity as LifecycleOwner) { total ->
                    if (total != null) pieChart.centerText = "$total \$"
                }
                homeViewModel.getExpenseTransactionsByDateRange(dates[0], dates[0]).observe(this@MainActivity as LifecycleOwner) { transactions ->
                    pieChartView.setupPieChart(pieChart, transactions)
                }
            } else {
                transactionViewModel.getIncomeTransactionsByDay(dates[0]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
                homeViewModel.getIncomesTotalAmount(dates[0], dates[0]).observe(this@MainActivity as LifecycleOwner) { total ->
                    if (total != null) pieChart.centerText = "$total \$"
                }
                homeViewModel.getIncomeTransactionsByDateRange(dates[0], dates[0]).observe(this@MainActivity as LifecycleOwner) { transactions ->
                    pieChartView.setupPieChart(pieChart, transactions)
                }
            }
        }
        else {
            if (currentTransactionTab == 0) {
                transactionViewModel.getExpenseTransactionsByDateInterval(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
                homeViewModel.getExpensesTotalAmount(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { total ->
                    if (total != null) pieChart.centerText = "$total \$"
                }
                homeViewModel.getExpenseTransactionsByDateRange(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { transactions ->
                    pieChartView.setupPieChart(pieChart, transactions)
                }
            } else {
                transactionViewModel.getIncomeTransactionsBDateInterval(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
                homeViewModel.getIncomesTotalAmount(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { total ->
                    if (total != null) pieChart.centerText = "$total \$"
                }
                homeViewModel.getIncomeTransactionsByDateRange(dates[0], dates[1]).observe(this@MainActivity as LifecycleOwner) { transactions ->
                    pieChartView.setupPieChart(pieChart, transactions)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                homePageSetup(this@MainActivity)
                return true
            }
            R.id.navigation_category -> {
                supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, CategoryFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.navigation_report -> {
                setContentView(R.layout.report)
                reportPageSetup(this@MainActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Montreal"))
        homePageSetup(this@MainActivity)
    }
}