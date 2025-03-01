package inf8402.polyargent.model.transaction

import android.graphics.Color
import android.graphics.Typeface
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class PieChartViewModel {

    fun setupPieChart(pieChart: PieChart, expenses: List<CategoryReport>) {
        val entries = ArrayList<PieEntry>()
        var colors = ArrayList<Int>()
        for (expense in expenses) {
            entries.add(PieEntry(expense.percentage.toFloat(), ""))
            colors.add(Color.parseColor(expense.colorHex))
        }

        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.colors = colors
        val data = PieData(dataSet)
        pieChart.data = data
        dataSet.sliceSpace = 4f
        pieChart.setCenterTextSize(25f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChart.holeRadius = 65f
        pieChart.invalidate()

        dataSet.setDrawValues(false)
        val legend = pieChart.legend
        legend.isEnabled = false
        pieChart.description.isEnabled = false
    }

}