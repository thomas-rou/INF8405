package inf8402.polyargent

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import inf8402.polyargent.model.DateTabViewModel
import inf8402.polyargent.model.PieChartViewModel
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import inf8402.polyargent.ui.screens.TransactionScreen
import inf8402.polyargent.ui.screens.setupTransactionScreen
import inf8402.polyargent.viewmodel.TransactionViewModel


class MainActivity : AppCompatActivity() {
    private var currentTransactionTab : Int = 0
    private val dateTab = DateTabViewModel()

    lateinit var adapter: TransactionScreen
    val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun pageSetup() {
        setContentView(R.layout.main_page)
        val pieChart: PieChart = findViewById(R.id.chart)
        val pieChartView = PieChartViewModel()
        pieChartView.setupPieChart(pieChart)
        manageSelectedDateRange()
        manageSelectedExpenseType()

        adapter = TransactionScreen(
            onDeleteClick = { transaction ->
                transactionViewModel.delete(transaction)
            },
            transactionViewModel = transactionViewModel
        )
            setupTransactionScreen()
    }

    private fun manageSelectedExpenseType() {
        val transactionTab : TabLayout = findViewById(R.id.tabs)

        transactionTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        transactionViewModel.allExpenses.observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                            adapter.submitList(transactionsGot)
                        }
                        currentTransactionTab = 0
                    }

                    1 -> {
                        transactionViewModel.allIncomes.observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                            adapter.submitList(transactionsGot)
                        }
                        currentTransactionTab = 1
                    }
                }
                manageSelectedDateRange()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun manageSelectedDateRange() {
        val tabLayout: TabLayout = findViewById(R.id.tabTimePeriod)
        val dateRangeText: TextView = findViewById(R.id.date_range_text)

        tabLayout.getTabAt(1)?.select()
        dateRangeText.text = dateTab.getDateRangeForTab(1)
        filterTransactionsByDate(1, dateRangeText.text.toString())

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                dateRangeText.text = dateTab.getDateRangeForTab(tab?.position ?: 1)
                filterTransactionsByDate(tab?.position, dateRangeText.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filterTransactionsByDate(tabPos: Int?, date: String) {
        if (tabPos == 0) {
            if (currentTransactionTab == 0) {
                transactionViewModel.getExpenseTransactionsByDay(dateTab.extractDates(date)[0]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
            } else {
                transactionViewModel.getIncomeTransactionsByDay(dateTab.extractDates(date)[0]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
            }
        }
        else {
            if (currentTransactionTab == 0) {
                transactionViewModel.getExpenseTransactionsByDateInterval(dateTab.extractDates(date)[0], dateTab.extractDates(date)[1]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
                }
            } else {
                transactionViewModel.getIncomeTransactionsBDateInterval(dateTab.extractDates(date)[0], dateTab.extractDates(date)[1]).observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                    adapter.submitList(transactionsGot)
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
                pageSetup()
                return true
            }
            R.id.navigation_category -> {
                setContentView(R.layout.category)
                return true
            }
            R.id.navigation_report -> {
                setContentView(R.layout.repport)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageSetup()
    }
}