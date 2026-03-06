package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import com.gilded.viewmodels.CurrentReceiptViewModel
import com.gilded.R
import com.gilded.models.Receipt
import com.gilded.recyclerview.ReceiptsRecyclerViewAdapter
import com.gilded.viewmodels.ReceiptsViewModel
import java.util.Date
import kotlin.random.Random

class HomeFragment : Fragment() {
    private lateinit var receiptsRecyclerView: RecyclerView
    private lateinit var receiptsRecyclerViewAdapter: ReceiptsRecyclerViewAdapter

    private lateinit var currentBalanceTextView: TextView

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)

        receiptsRecyclerView = homeFragmentView.findViewById(R.id.receipts)
        currentBalanceTextView = homeFragmentView.findViewById(R.id.currentBalance)

        receiptsRecyclerView.layoutManager = LinearLayoutManager(context)

        receiptsRecyclerViewAdapter = ReceiptsRecyclerViewAdapter(
            receipts = receiptsViewModel.getReceipts(),
            onReceiptClick = { receipt ->

                currentReceiptViewModel.setReceipt(receipt)

                val receiptViewerFragment = ReceiptViewerFragment()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, receiptViewerFragment)
                    ?.commit()
            }
        )

        receiptsRecyclerView.adapter = receiptsRecyclerViewAdapter

        receiptsViewModel.receipts.observe(viewLifecycleOwner) {
            receiptsRecyclerViewAdapter.updateList(receiptsViewModel.getReceipts())
            updateCurrentBalance()
        }

        updateCurrentBalance()

        return homeFragmentView
    }

    fun updateCurrentBalance() {
        var currentBalance = 0.0

        for (receipt in receiptsViewModel.getReceipts()) {
            currentBalance = currentBalance + receipt.amount
        }

        currentBalanceTextView.text = "${currentBalance.toString()}€"
    }

}