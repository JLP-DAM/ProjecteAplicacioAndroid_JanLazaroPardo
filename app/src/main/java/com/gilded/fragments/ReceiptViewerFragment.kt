package com.gilded.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import com.gilded.viewmodels.CurrentReceiptViewModel
import com.gilded.R
import com.gilded.viewmodels.FilterViewModel
import com.gilded.viewmodels.ReceiptsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.sign

class ReceiptViewerFragment : Fragment() {
    private val receiptsViewModel: ReceiptsViewModel by activityViewModels()
    private val currentReceiptViewModel: CurrentReceiptViewModel by activityViewModels()

    private val filterViewModel: FilterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptViewer = inflater.inflate(R.layout.fragment_receipt_viewer, container, false)

        val recipientTextView: TextView = receiptViewer.findViewById(R.id.recipient)
        val amountTextView: TextView = receiptViewer.findViewById(R.id.amount)
        val timestampTextView: TextView = receiptViewer.findViewById(R.id.timestamp)
        val categoryTextView: TextView = receiptViewer.findViewById(R.id.category)
        val editImageButton: ImageButton = receiptViewer.findViewById(R.id.edit)

        val deleteCardView = receiptViewer.findViewById<CardView>(R.id.delete)
        val goBackButton = receiptViewer.findViewById<ImageButton>(R.id.goBack)

        val spentOnCategoryTextView: TextView = receiptViewer.findViewById(R.id.spentOnCategory)
        val spentOnCategoryAmountTextView: TextView = receiptViewer.findViewById(R.id.spentOnCategoryAmount)
        val viewSpentOnCategoryAmountTextView: TextView = receiptViewer.findViewById(R.id.viewSpentOnCategory)
        val viewSpentOnCategoryAmountButton: Button = receiptViewer.findViewById(R.id.viewSpentOnCategoryButton)

        fun goBack() {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        goBackButton.setOnClickListener { goBack() }

        deleteCardView.setOnClickListener {
            receiptsViewModel.removeReceipt(receiptsViewModel.getReceiptIndex(currentReceiptViewModel.receipt.value))

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

            var totalReceipts = 0
            var totalAmount = 0.0

            for (otherReceipt in receiptsViewModel.getReceipts()) {
                if (otherReceipt.category != receipt.category) {continue}

                totalReceipts++

                if (sign(otherReceipt.amount) != sign(receipt.amount)) {continue}

                totalAmount = totalAmount + otherReceipt.amount
            }

            if (receipt.amount < 0) {
                spentOnCategoryTextView.text = "Pagat en ${receipt.category}"
            } else {
                spentOnCategoryTextView.text = "Cobrat en ${receipt.category}"
            }

            spentOnCategoryAmountTextView.text = "${totalAmount}€"
            viewSpentOnCategoryAmountTextView.text = "Veure ${totalReceipts} transaccions"
        }

        viewSpentOnCategoryAmountButton.setOnClickListener {
            if (filterViewModel.filteredCategory.value == currentReceiptViewModel.receipt.value!!.category) {
                filterViewModel.setFilteredCategory(null)
            } else {
                filterViewModel.setFilteredCategory(currentReceiptViewModel.receipt.value!!.category)
            }

            goBack()
        }

        editImageButton.setOnClickListener {
            val receiptEditorFragment = ReceiptEditorFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, receiptEditorFragment)
                ?.commit()
        }

        return receiptViewer
    }
}