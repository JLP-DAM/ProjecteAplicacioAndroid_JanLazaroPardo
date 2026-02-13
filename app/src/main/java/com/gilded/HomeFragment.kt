package com.gilded

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.material3.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import kotlin.random.Random
import kotlin.random.nextLong

class HomeFragment : Fragment() {
    private lateinit var receiptsRecyclerView: RecyclerView
    private lateinit var receiptsRecyclerViewAdapter: ReceiptsRecyclerViewAdapter

    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)

        if (homeFragmentView != null) {
            receiptsRecyclerView = homeFragmentView.findViewById(R.id.receipts)
        }

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
        }

        homeFragmentView.findViewById<Button>(R.id.testAddButton).setOnClickListener {
            receiptsViewModel.addReceipt(Receipt("Test", Random.nextDouble(-30.0, 30.0), Random.nextLong(0, 10000000), "Test"))
        }

        return homeFragmentView
    }

}