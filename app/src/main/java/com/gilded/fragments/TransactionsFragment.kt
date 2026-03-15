package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.google.android.material.chip.ChipGroup
import java.util.Date
import kotlin.collections.iterator
import kotlin.collections.set
import kotlin.getValue
import kotlin.math.abs
import kotlin.math.max

class TransactionsFragment : Fragment() {
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()
    
    private val DAY_LENGTH = 86400000
    private var daysBackwards: Long = 7

    private var mode = true

    lateinit var transactionsFragmentView: View

    lateinit var timeBoundariesChipGroup: ChipGroup

    lateinit var incomeButton: Button
    lateinit var expensesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        transactionsFragmentView = inflater.inflate(R.layout.fragment_transactions, container, false)

        timeBoundariesChipGroup = transactionsFragmentView.findViewById(R.id.timeBoundaries)

        incomeButton = transactionsFragmentView.findViewById(R.id.income)
        expensesButton = transactionsFragmentView.findViewById(R.id.expenses)

        incomeButton.setOnClickListener {
            mode = true
            update()
        }

        expensesButton.setOnClickListener {
            mode = false
            update()
        }

        setupChips()
        update()

        return transactionsFragmentView
    }

    private fun setupChips() {
        var oldestReceiptTimestampDay: Long = 0

        for (receipt in receiptsViewModel.getReceipts()) {
            if (oldestReceiptTimestampDay >= (Date().time - Date(receipt.timestamp).time) / DAY_LENGTH) {continue}

            oldestReceiptTimestampDay = (Date().time - Date(receipt.timestamp).time) / DAY_LENGTH
        }

        oldestReceiptTimestampDay = max(6, oldestReceiptTimestampDay) + 1

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
        incomeButton.setBackgroundColor(if (mode) resources.getColor(R.color.black_tonal2) else resources.getColor(R.color.black_tonal1))
        expensesButton.setBackgroundColor(if (!mode) resources.getColor(R.color.black_tonal2) else resources.getColor(R.color.black_tonal1))

        createPieChart()
        createLineChart()
    }

    fun createPieChart() {
        val pieChart: PieChart = transactionsFragmentView.findViewById(R.id.pieChart)

        val expenseByCategoryHashMap: HashMap<String, Double> = HashMap()

        val minimumBoundary = Date(Date().time - (daysBackwards * DAY_LENGTH)).time

        val maximumBoundary = Date().time

        for (receipt in receiptsViewModel.getReceipts()) {
            if ((!mode && receipt.amount >= 0) || (mode && receipt.amount < 0)) {
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

        pieChart.data = data
        pieChart.centerTextRadiusPercent = 0f
        pieChart.isDrawHoleEnabled = true
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = true
        pieChart.description.text = ""
        pieChart.isDrawHoleEnabled = false

        pieChart.invalidate()
    }

    fun createLineChart() {
        val lineChart: LineChart = transactionsFragmentView.findViewById(R.id.lineChart)

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

        val minimumBoundary = Date(Date().time - (daysBackwards * DAY_LENGTH)).time

        val maximumBoundary = Date().time

        for (receipt in receiptsViewModel.getReceipts()) {
            if ((!mode && receipt.amount >= 0) || (mode && receipt.amount < 0)) {
                continue
            }

            if (receipt.timestamp < minimumBoundary || receipt.timestamp > maximumBoundary) {
                continue
            }

            val date: Date = Date(receipt.timestamp)

            for (entry in totalArrayList) {
                if (entry.x.toLong() != daysBackwards - (Date(Date().time - date.time).time / DAY_LENGTH)) {
                    continue
                }

                entry.y = entry.y + abs(receipt.amount.toFloat())
            }

            val categoryArrayList = categoryArrayListHashMap[receipt.category] ?: continue

            for (entry in categoryArrayList) {
                if (entry.x.toLong() != daysBackwards - (Date(Date().time - date.time).time / DAY_LENGTH)) {
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
        lineChart.data = data
        lineChart.setBackgroundColor(resources.getColor(R.color.black))
        lineChart.legend.textColor = white
        lineChart.xAxis.textColor = white
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisLeft.textColor = white
        lineChart.axisRight.setDrawLabels(false)
        lineChart.description.text = ""
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisLeft.mDecimals = 0
        lineChart.animateXY(2000, 2000, Easing.EaseInSine)
    }
}