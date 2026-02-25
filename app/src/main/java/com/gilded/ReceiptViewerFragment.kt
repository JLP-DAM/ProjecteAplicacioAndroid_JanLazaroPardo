package com.gilded

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import java.text.SimpleDateFormat
import java.util.Date

class ReceiptViewerFragment : Fragment() {
    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptViewer = inflater.inflate(R.layout.fragment_receipt_viewer, container, false)

        val recipientTextView: TextView = receiptViewer.findViewById(R.id.recipient)
        val amountTextView: TextView = receiptViewer.findViewById(R.id.amount)
        val timestampTextView: TextView = receiptViewer.findViewById(R.id.timestamp)
        val categoryTextView: TextView = receiptViewer.findViewById(R.id.category)

        val deleteCardView = receiptViewer.findViewById<CardView>(R.id.delete)
        val goBackButton = receiptViewer.findViewById<ImageButton>(R.id.goBack)

        fun goBack() {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        goBackButton.setOnClickListener { goBack() }

        deleteCardView.setOnClickListener {
            goBack()
        }

        currentReceiptViewModel.receipt.observe(viewLifecycleOwner) {receipt ->
            recipientTextView.text = receipt.recipient

            amountTextView.text = String.format("%s%s€", if (receipt.amount > 0f) "+" else "", receipt.amount)

            val timestampDate = Date(receipt.timestamp)
            val formatter = SimpleDateFormat("MMMM dd, kk:mm")
            val formattedDate = formatter.format(timestampDate)

            timestampTextView.text = formattedDate

            categoryTextView.text = receipt.category
        }

        return receiptViewer
    }
}