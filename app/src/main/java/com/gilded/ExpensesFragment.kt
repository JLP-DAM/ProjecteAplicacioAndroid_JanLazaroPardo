package com.gilded

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import kotlin.collections.set
import kotlin.getValue
import kotlin.math.abs

class ExpensesFragment : Fragment() {
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()

    private val calendar: Calendar = Calendar.getInstance()
    private val currentTimeBoundary: HashMap<String, Int> = HashMap();
    private val mode: String = "month";

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val expensesView = inflater.inflate(R.layout.fragment_expenses, container, false)

        currentTimeBoundary["year"] = 2026
        currentTimeBoundary["month"] = 2

        createPieChart(expensesView)
        createLineChart(expensesView)

        return expensesView
    }

    fun createPieChart(expensesView: View) {
        val expensesPieChart: PieChart = expensesView.findViewById(R.id.expensesPieChart)

        val expenseByCategoryHashMap: HashMap<String, Double> = HashMap()

        for (receipt in receiptsViewModel.getReceipts()) {
            if (receipt.amount >= 0) {
                continue
            }

            expenseByCategoryHashMap[receipt.category] =
                ((expenseByCategoryHashMap[receipt.category] ?: 0.0)) + abs(receipt.amount)
        }

        val dataEntries: ArrayList<PieEntry> = ArrayList()

        for (expense in expenseByCategoryHashMap) {
            dataEntries.add(PieEntry(expense.value.toFloat(), expense.key))
        }

        val dataSet = PieDataSet(dataEntries, "")

        val colorSet: ArrayList<Int> = ArrayList()
        colorSet.add(Color.rgb(255, 107, 107))
        colorSet.add(Color.rgb(173, 232, 244))
        colorSet.add(Color.rgb(216, 243, 220))
        colorSet.add(Color.rgb(255, 230, 109))
        dataSet.setColors(colorSet)

        val data = PieData(dataSet)

        expensesPieChart.data = data
        expensesPieChart.centerTextRadiusPercent = 0f
        expensesPieChart.isDrawHoleEnabled = true
        expensesPieChart.legend.isEnabled = false
        expensesPieChart.description.isEnabled = true
        expensesPieChart.setDrawHoleEnabled(false);
    }

    fun createLineChart(expensesView: View) {
        val expensesLineChart: LineChart = expensesView.findViewById(R.id.expensesLineChart)

        val valuesArrayList = ArrayList<Entry>()

        val monthLength: Int = YearMonth.of(currentTimeBoundary["year"]!!, currentTimeBoundary["month"]!! + 1).lengthOfMonth()

        val minimumBoundary = Date(
            currentTimeBoundary["year"]!!,
            currentTimeBoundary["month"]!!,
            1
        ).time.toLong()

        val maximumBoundary = Date(
            currentTimeBoundary["year"]!!,
            currentTimeBoundary["month"]!!,
            monthLength
        ).time.toLong()

        for (day in 1..monthLength) {
            valuesArrayList.add(Entry(day.toFloat(), 0f))
        }



        for (receipt in receiptsViewModel.getReceipts()) {
            if (receipt.amount >= 0) {
                continue
            }



            if (receipt.timestamp < minimumBoundary || receipt.timestamp > maximumBoundary) {
                continue
            }

            val foundEntry: Entry?

            val date: Date = Date(receipt.timestamp)

            for (entry in valuesArrayList) {
                if (entry.x != date.date.toFloat()) {
                    continue
                }

                entry.y = entry.y + abs(receipt.amount.toFloat())
            }
        }

        val lineDataSet: LineDataSet = LineDataSet(valuesArrayList, "")

        lineDataSet.color = resources.getColor(R.color.blue)

        lineDataSet.circleRadius = 0f
        lineDataSet.setDrawFilled(false)
        lineDataSet.setDrawCircles(false)
        lineDataSet.valueTextSize = 0f
        lineDataSet.fillColor = resources.getColor(R.color.green)
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        val data = LineData(lineDataSet)
        expensesLineChart.data = data
        expensesLineChart.setBackgroundColor(resources.getColor(R.color.white))
        expensesLineChart.animateXY(2000, 2000, Easing.EaseInSine)
    }
}