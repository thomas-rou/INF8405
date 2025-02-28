package inf8402.polyargent.model.home

import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class PieChartViewModel {

    fun setupPieChart(pieChart: PieChart) {
        val entries = listOf(PieEntry(40f), PieEntry(60f))
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 4f
        pieChart.data = PieData(dataSet)
        pieChart.centerText = "1000.00 \$"
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