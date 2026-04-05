package com.gilded.recyclerview

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gilded.R
import com.gilded.models.Receipt
import com.gilded.services.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.text.SimpleDateFormat
import java.util.Date

class ReceiptsRecyclerViewHolder(
    receiptCardView: View,
    private val onReceiptClick: (Receipt) -> Unit
) : RecyclerView.ViewHolder(receiptCardView) {
    private val recipientTextView: TextView = receiptCardView.findViewById(R.id.recipient)
    private val amountTextView: TextView = receiptCardView.findViewById(R.id.amount)
    private val timestampTextView: TextView = receiptCardView.findViewById(R.id.timestamp)
    private val sectionTextView: TextView = receiptCardView.findViewById(R.id.category)

    private val receiptButton: Button = receiptCardView.findViewById(R.id.button)
    fun bind(receipt: Receipt) {
        recipientTextView.text = receipt.recipient

        MainScope().launch(Dispatchers.Main) {
            val currencySymbol = SettingsDataStore.getCurrencySymbol(recipientTextView.context)
            amountTextView.text = String.format("%s%s%s", if (receipt.amount > 0f) "+" else "", receipt.amount, currencySymbol.first())
        }

        val timestampDate = Date(receipt.timestamp)
        val formatter = SimpleDateFormat("MMMM dd, kk:mm")
        val formattedDate = formatter.format(timestampDate)

        timestampTextView.text = formattedDate

        sectionTextView.text = receipt.category

        receiptButton.setOnClickListener { onReceiptClick(receipt) }
    }
}