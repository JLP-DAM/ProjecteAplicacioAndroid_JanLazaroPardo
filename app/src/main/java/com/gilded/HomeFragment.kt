package com.gilded

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    private lateinit var receiptsRecyclerView: RecyclerView
    private lateinit var receiptsRecyclerViewAdapter: ReceiptsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)

        if (homeFragmentView != null) {
            receiptsRecyclerView = homeFragmentView.findViewById<RecyclerView>(R.id.receipts)
        }

        receiptsRecyclerView.layoutManager = LinearLayoutManager(context)

        receiptsRecyclerViewAdapter = ReceiptsRecyclerViewAdapter(
            receipts = TestReceipts.receipts,
            onItemClick = { receipt ->
                Toast.makeText(
                    context,
                    "Has obert el rebut: ${receipt.recipient} (Visualitzador de rebut en proces...)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        receiptsRecyclerView.adapter = receiptsRecyclerViewAdapter

        return homeFragmentView
    }

}