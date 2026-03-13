package com.gilded.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gilded.R
import com.gilded.recyclerview.ReceiptsRecyclerViewAdapter
import com.gilded.viewmodels.CurrentReceiptViewModel
import com.gilded.viewmodels.ReceiptsViewModel


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

        val helpButton: Button = homeFragmentView.findViewById(R.id.help)
        val settingsButton: ImageButton = homeFragmentView.findViewById(R.id.settings)

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

        helpButton.setOnClickListener {
            val helpFragment = HelpFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, helpFragment)
                ?.commit()
        }

        settingsButton.setOnClickListener {
            val settingsFragment = SettingsFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, settingsFragment)
                ?.commit()
        }

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.getAdapterPosition()
                receiptsViewModel.removeReceipt(position)
                receiptsRecyclerViewAdapter.notifyDataSetChanged()
            }
        }

        val receiptTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        receiptTouchHelper.attachToRecyclerView(receiptsRecyclerView)

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