package com.gilded.services

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object UsageData {
    var receiptCreations = 0
    var receiptDeletions = 0
    var usageTime = 0.0
    var mostFrequentedFragments = HashMap<String, Int>()
}