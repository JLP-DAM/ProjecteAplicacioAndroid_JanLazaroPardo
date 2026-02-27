package com.gilded

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlin.getValue
import kotlin.math.abs

class ExpensesFragment : Fragment() {
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val expensesView = inflater.inflate(R.layout.fragment_expenses, container, false)

        val expensesPieChart: PieChart = expensesView.findViewById(R.id.expensesPieChart)


        val expenseByCategoryHashMap: HashMap<String, Double> = HashMap()

        for (receipt in receiptsViewModel.getReceipts()) {
            if (receipt.amount >= 0) {continue}

            expenseByCategoryHashMap[receipt.category] = ((expenseByCategoryHashMap[receipt.category] ?: 0.0)) + abs(receipt.amount)
        }

        val dataEntries: ArrayList<PieEntry> = ArrayList()

        for (expense in expenseByCategoryHashMap) {
            Log.d(expense.key, expense.value.toString())
            dataEntries.add(PieEntry(expense.value.toFloat(), expense.key))
        }

       val  dataSet = PieDataSet(dataEntries, "")

        val colorSet = java.util.ArrayList<Int>()
        colorSet.add(Color.rgb(255,107,107))
        colorSet.add(Color.rgb(173,232,244))
        colorSet.add(Color.rgb(216,243,220))
        colorSet.add(Color.rgb(255,230,109))
        dataSet.setColors(colorSet)

        val data = PieData(dataSet)

        expensesPieChart.data = data
        expensesPieChart.centerTextRadiusPercent = 0f
        expensesPieChart.isDrawHoleEnabled = true
        expensesPieChart.legend.isEnabled = false
        expensesPieChart.description.isEnabled = true
        expensesPieChart.setDrawHoleEnabled(false);

        return expensesView
    }
}