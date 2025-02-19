package inf8402.polyargent

import android.annotation.SuppressLint
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
import com.google.android.material.tabs.TabLayout
import inf8402.polyargent.ui.fragments.CategoryFragment
import inf8402.polyargent.ui.screens.TransactionScreen
import inf8402.polyargent.ui.screens.setupTransactionScreen
import inf8402.polyargent.viewmodel.TransactionViewModel


class MainActivity : AppCompatActivity() {
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

        adapter = TransactionScreen(
            onDeleteClick = { transaction ->
                transactionViewModel.delete(transaction)
            },
            transactionViewModel = transactionViewModel
        )
            setupTransactionScreen()
    }

    private fun manageSelectedDateRange() {
        val tabLayout: TabLayout = findViewById(R.id.tabTimePeriod)
        val dateRangeText: TextView = findViewById(R.id.date_range_text)
        val transactionTab : TabLayout = findViewById(R.id.tabs)

        transactionTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        transactionViewModel.allExpenses.observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                            adapter.submitList(transactionsGot)
                        }

                    }

                    1 -> {
                        transactionViewModel.allIncomes.observe(this@MainActivity as LifecycleOwner) { transactionsGot ->
                            adapter.submitList(transactionsGot)
                        }

                    }
                }

        }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val dateTab = DateTabViewModel()
                val dateRange = dateTab.getDateRangeForTab(tab?.position ?: 0)
                dateRangeText.text = dateRange
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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
                supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, CategoryFragment())
                    .addToBackStack(null)
                    .commit()
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