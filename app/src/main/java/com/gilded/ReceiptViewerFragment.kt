package com.gilded

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView

class ReceiptViewerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptViewer = inflater.inflate(R.layout.fragment_receipt_viewer, container, false)

        val goBackButton = receiptViewer.findViewById<ImageButton>(R.id.go_back)
        val deleteCardView = receiptViewer.findViewById<CardView>(R.id.delete)

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

        return receiptViewer
    }
}