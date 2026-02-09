package com.gilded

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class ReceiptsRecyclerViewHolder(
    receiptCardView: View,
    private val onItemClick: (Receipt) -> Unit
) : RecyclerView.ViewHolder(receiptCardView) {

    private val recipientTextView: TextView = receiptCardView.findViewById(R.id.recipient)
    private val amountTextView: TextView = receiptCardView.findViewById(R.id.amount)
    private val timestampTextView: TextView = receiptCardView.findViewById(R.id.timestamp)
    private val sectionTextView: TextView = receiptCardView.findViewById(R.id.section)

    fun bind(receipt: Receipt) {
        recipientTextView.text = receipt.recipient

        amountTextView.text = String.format("%s%s€", if (receipt.amount > 0f) "+" else "", receipt.amount)

        val timestampDate = Date(receipt.timestamp)
        val formatter = SimpleDateFormat("MMMM dd, kk:mm")
        val formattedDate = formatter.format(timestampDate)

        timestampTextView.text = formattedDate

        sectionTextView.text = receipt.section
    }
}