package com.gilded.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.services.PreferencesKeys
import com.gilded.services.UsageDataStore
import com.gilded.services.preferencesDataStore
import com.gilded.viewmodels.SettingsViewModel
import com.gilded.viewmodels.UsageDataViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Currency
import kotlin.getValue


class UsageDataFragment : Fragment() {
    private val usageDataViewModel: UsageDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val usageDataFragmentView = inflater.inflate(R.layout.fragment_usage_data, container, false)

        val fragmentHorizontalBarChart: HorizontalBarChart = usageDataFragmentView.findViewById(R.id.fragments)
        val receiptActionBarChart: BarChart = usageDataFragmentView.findViewById(R.id.receipts)

        val closeButton: ImageButton = usageDataFragmentView.findViewById(R.id.close)

        val emissionsTextView: TextView = usageDataFragmentView.findViewById(R.id.emissions)

        val co2KGPerHour = 0.17260273972
        val usageHours = usageDataViewModel.usageTime.value!! / (1000 * 60 * 60)

        emissionsTextView.setText("Emissions de Co2: " + String.format("%.2f", usageHours * co2KGPerHour) + " KG")

        lifecycleScope.launch(Dispatchers.Main) {

            val chartItems = (UsageDataStore.getAllKeys(requireContext()) ?: emptySet())
                .map { key ->

                    val fragmentName = key.name

                    val usageCount = UsageDataStore
                        .getFragment(requireContext(), fragmentName)
                        .firstOrNull() ?: 0

                    fragmentName to usageCount
                }
                .sortedByDescending { it.second }

            val entries = chartItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.second.toFloat())
            }

            val labels = chartItems.map { it.first }

            val dataSet = BarDataSet(entries, "").apply {
                color = resources.getColor(R.color.orange)
                valueTextColor = resources.getColor(R.color.white)
                valueTextSize = 10f
            }

            val barData = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            fragmentHorizontalBarChart.apply {

                data = barData

                description.isEnabled = false
                legend.isEnabled = false
                setFitBars(true)

                setDrawValueAboveBar(true)

                animateY(800)

                setExtraOffsets(
                    120f,
                    16f,
                    24f,
                    16f
                )

                xAxis.apply {

                    valueFormatter = IndexAxisValueFormatter(labels)

                    granularity = 1f
                    labelCount = labels.size

                    position = XAxis.XAxisPosition.BOTTOM

                    setDrawGridLines(false)
                    setDrawAxisLine(true)

                    textColor = resources.getColor(R.color.white)

                    textSize = 11f
                }

                axisLeft.apply {

                    axisMinimum = 0f

                    setDrawGridLines(false)
                    setDrawAxisLine(true)

                    textColor = resources.getColor(R.color.white)

                    textSize = 10f
                }

                axisRight.isEnabled = false

                setPinchZoom(false)
                setScaleEnabled(false)

                invalidate()
            }
        }

        val chartItems = listOf(
            "Rebuts creats" to (usageDataViewModel.receiptCreations.value ?: 0),
            "Rebuts borrats" to (usageDataViewModel.receiptDeletions.value ?: 0)
        ).sortedByDescending { it.second }

        val entries = chartItems.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.second.toFloat())
        }

        val labels = chartItems.map { it.first }

        val dataSet = BarDataSet(entries, "").apply {
            color = resources.getColor(R.color.green)
            valueTextColor = resources.getColor(R.color.white)
            valueTextSize = 12f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        receiptActionBarChart.apply {

            data = barData

            description.isEnabled = false
            legend.isEnabled = false

            setFitBars(true)

            animateY(800)

            setExtraOffsets(
                16f,
                16f,
                16f,
                16f
            )

            xAxis.apply {

                valueFormatter = IndexAxisValueFormatter(labels)

                position = XAxis.XAxisPosition.BOTTOM

                granularity = 1f
                labelCount = labels.size

                setDrawGridLines(false)
                setDrawAxisLine(false)

                textColor = resources.getColor(R.color.white)

                textSize = 11f
            }

            axisLeft.apply {

                axisMinimum = 0f

                setDrawGridLines(false)
                setDrawAxisLine(false)

                textColor = resources.getColor(R.color.white)

                textSize = 10f
            }

            axisRight.isEnabled = false

            setScaleEnabled(false)
            setPinchZoom(false)

            invalidate()
        }

        closeButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, SettingsFragment())
                ?.commit()
        }

        return usageDataFragmentView
    }
}