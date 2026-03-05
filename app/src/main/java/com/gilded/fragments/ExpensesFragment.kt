package com.gilded.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import com.gilded.viewmodels.CategoriesViewModel
import com.gilded.R
import com.gilded.viewmodels.ReceiptsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import kotlin.collections.iterator
import kotlin.collections.set
import kotlin.getValue
import kotlin.math.abs

class ExpensesFragment : Fragment() {
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()
    private var daysBackwards: Int = 7

    lateinit var expensesView: View

    lateinit var timeBoundariesChipGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        expensesView = inflater.inflate(R.layout.fragment_expenses, container, false)

        timeBoundariesChipGroup = expensesView.findViewById(R.id.timeBoundaries)

        setupChips()
        update()

        return expensesView
    }

    private fun setupChips() {
        var oldestReceiptTimestampDay = 1

        for (receipt in receiptsViewModel.getReceipts()) {
            if (oldestReceiptTimestampDay <= (Date().time / 86400000) - (Date(receipt.timestamp).time / 86400000)) {continue}

            oldestReceiptTimestampDay = (Date(receipt.timestamp).time / 86400000).toInt()
        }

        timeBoundariesChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            daysBackwards = when {
                checkedIds.contains(R.id.lastWeek) -> 7
                checkedIds.contains(R.id.lastMonth) -> 30
                checkedIds.contains(R.id.lastYear) -> 365
                checkedIds.contains(R.id.allTime) -> oldestReceiptTimestampDay
                else -> 7
            }

            update()
        }
    }

    fun update() {
        createPieChart(expensesView)
        createLineChart(expensesView)
    }

    fun createPieChart(expensesView: View) {
        val expensesPieChart: PieChart = expensesView.findViewById(R.id.expensesPieChart)

        val expenseByCategoryHashMap: HashMap<String, Double> = HashMap()

        val minimumBoundary = Date(Date().time - (daysBackwards * 86400000)).time

        val maximumBoundary = Date().time

        for (receipt in receiptsViewModel.getReceipts()) {
            if (receipt.amount >= 0) {
                continue
            }

            if (receipt.timestamp < minimumBoundary || receipt.timestamp > maximumBoundary) {
                continue
            }

            expenseByCategoryHashMap[receipt.category] =
                ((expenseByCategoryHashMap[receipt.category] ?: 0.0)) + abs(receipt.amount)
        }

        val dataEntries: ArrayList<PieEntry> = ArrayList()
        val colorsArrayList: ArrayList<Int> = ArrayList()

        for (categoryExpenses in expenseByCategoryHashMap) {
            dataEntries.add(PieEntry(categoryExpenses.value.toFloat(), categoryExpenses.key))
            colorsArrayList.add(categoriesViewModel.getCategory(categoryExpenses.key)?.color ?: 0)
        }

        val dataSet = PieDataSet(dataEntries, "")
        dataSet.setColors(colorsArrayList)

        val data = PieData(dataSet)

        expensesPieChart.data = data
        expensesPieChart.centerTextRadiusPercent = 0f
        expensesPieChart.isDrawHoleEnabled = true
        expensesPieChart.legend.isEnabled = false
        expensesPieChart.description.isEnabled = true
        expensesPieChart.description.text = ""
        expensesPieChart.isDrawHoleEnabled = false;
    }

    fun createLineChart(expensesView: View) {
        val expensesLineChart: LineChart = expensesView.findViewById(R.id.expensesLineChart)

        val totalArrayList: ArrayList<Entry> = ArrayList()

        val categoryArrayListHashMap: HashMap<String, ArrayList<Entry>> = HashMap()

        for (category in categoriesViewModel.getCategories()) {
            categoryArrayListHashMap[category.name] = ArrayList()
        }

        for (day in 1..daysBackwards) {
            totalArrayList.add(Entry(day.toFloat(), 0f))

            for (categoryArrayList in categoryArrayListHashMap.values) {
                categoryArrayList.add(Entry(day.toFloat(), 0f))
            }
        }

        val minimumBoundary = Date(Date().time - (daysBackwards * 86400000)).time

        val maximumBoundary = Date().time

        for (receipt in receiptsViewModel.getReceipts()) {
            if (receipt.amount >= 0) {
                continue
            }

            if (receipt.timestamp < minimumBoundary || receipt.timestamp > maximumBoundary) {
                continue
            }

            val date: Date = Date(receipt.timestamp)

            for (entry in totalArrayList) {
                if (entry.x.toInt() != daysBackwards - (Date(Date().time - date.time).time / 86400000).toInt()) {
                    continue
                }

                entry.y = entry.y + abs(receipt.amount.toFloat())
            }

            val categoryArrayList = categoryArrayListHashMap[receipt.category] ?: continue

            Log.d("Days from", (Date(Date().time - date.time).time / 86400000).toString())

            for (entry in categoryArrayList) {
                if (entry.x.toInt() != daysBackwards - (Date(Date().time - date.time).time / 86400000).toInt()) {
                    continue
                }

                entry.y = entry.y + abs(receipt.amount.toFloat())
            }
        }

        val totalLineDataSet = LineDataSet(totalArrayList, "Total")

        totalLineDataSet.color = resources.getColor(R.color.blue)

        totalLineDataSet.circleRadius = 0f
        totalLineDataSet.setDrawFilled(false)
        totalLineDataSet.setDrawCircles(false)
        totalLineDataSet.valueTextSize = 0f
        totalLineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        dataSets.add(totalLineDataSet)

        for (categoryArrayList in categoryArrayListHashMap) {
            var totalPrice = 0f

            for (expenseEntry in categoryArrayList.value) {
                totalPrice = totalPrice + expenseEntry.y
            }

            Log.d(categoryArrayList.key, totalPrice.toString())

            if (totalPrice <= 0f) {continue}

            val categoryLineDataSet = LineDataSet(categoryArrayList.value, categoryArrayList.key)

            categoryLineDataSet.color = categoriesViewModel.getCategory(categoryArrayList.key)?.color ?: 0

            categoryLineDataSet.circleRadius = 0f
            categoryLineDataSet.setDrawFilled(false)
            categoryLineDataSet.setDrawCircles(false)
            categoryLineDataSet.valueTextSize = 0f
            categoryLineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

            dataSets.add(categoryLineDataSet)
        }

        val white = resources.getColor(R.color.white)

        val data = LineData(dataSets)
        expensesLineChart.data = data
        expensesLineChart.setBackgroundColor(resources.getColor(R.color.black))
        expensesLineChart.legend.textColor = white
        expensesLineChart.xAxis.textColor = white
        expensesLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        expensesLineChart.axisLeft.textColor = white
        expensesLineChart.axisRight.setDrawLabels(false)
        expensesLineChart.description.text = ""
        expensesLineChart.axisLeft.axisMinimum = 0f
        expensesLineChart.axisLeft.mDecimals = 0
        expensesLineChart.animateXY(2000, 2000, Easing.EaseInSine)
    }
}