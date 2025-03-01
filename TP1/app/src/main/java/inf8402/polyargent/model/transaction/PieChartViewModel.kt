package inf8402.polyargent.model.transaction

import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class PieChartViewModel {

    fun setupPieChart(pieChart: PieChart, expenses: List<CategoryReport>) {
        val entries = ArrayList<PieEntry>()
        for (expense in expenses) {
            entries.add(PieEntry(expense.percentage.toFloat(), ""))
        }

        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val data = PieData(dataSet)
        pieChart.data = data
        dataSet.sliceSpace = 4f
        pieChart.centerText = "0.00 \$"
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