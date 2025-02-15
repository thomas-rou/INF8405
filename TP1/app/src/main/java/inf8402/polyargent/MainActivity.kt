package inf8402.polyargent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import inf8402.polyargent.ui.screens.ExpenseScreen
import inf8402.polyargent.ui.screens.setupExpenseScreen
import inf8402.polyargent.viewmodel.ExpenseViewModel
import inf8402.polyargent.model.DateTabViewModel
import inf8402.polyargent.model.PieChartViewModel
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ExpenseScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        val pieChart: PieChart = findViewById(R.id.chart)
        val pieChartView = PieChartViewModel()
        pieChartView.setupPieChart(pieChart)

        manageSelectedDateRange()

        adapter = ExpenseScreen { expense -> expenseViewModel.delete(expense) }
        setupExpenseScreen(expenseViewModel, adapter, this)
    }

    private fun manageSelectedDateRange() {
        val tabLayout: TabLayout = findViewById(R.id.tabTimePeriod)
        val dateRangeText: TextView = findViewById(R.id.date_range_text)

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

    private val expenseViewModel: ExpenseViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                setContentView(R.layout.main_page)
                adapter = ExpenseScreen { expense -> expenseViewModel.delete(expense) }
                setupExpenseScreen(expenseViewModel, adapter, this)
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

}