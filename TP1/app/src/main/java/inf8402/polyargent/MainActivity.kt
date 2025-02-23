package inf8402.polyargent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import inf8402.polyargent.model.transaction.TimeFrequency
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.ui.screens.ReportScreen
import inf8402.polyargent.ui.fragments.CategoryFragment
import inf8402.polyargent.ui.screens.TransactionScreen
import inf8402.polyargent.ui.screens.homePageSetup
import inf8402.polyargent.ui.screens.reportPageSetup
import inf8402.polyargent.viewmodel.ReportViewModel
import inf8402.polyargent.viewmodel.TransactionViewModel
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
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