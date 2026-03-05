package com.gilded.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gilded.R
import com.gilded.models.Receipt

class ReceiptsRecyclerViewAdapter(
    private var receipts: List<Receipt>,
    private val onReceiptClick: (Receipt) -> Unit
) : RecyclerView.Adapter<ReceiptsRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptsRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.receipt, parent, false)
        return ReceiptsRecyclerViewHolder(view, onReceiptClick)
    }

    override fun getItemCount(): Int = receipts.size

    override fun onBindViewHolder(holder: ReceiptsRecyclerViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.bind(receipt)
    }

    fun updateList(newList: List<Receipt>) {
        receipts = newList
        notifyDataSetChanged()
    }
}