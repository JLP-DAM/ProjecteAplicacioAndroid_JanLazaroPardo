package com.gilded

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class ReceiptCreatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val receiptCreator = inflater.inflate(R.layout.fragment_receipt_creator, container, false)

        val goBackButton = receiptCreator.findViewById<ImageButton>(R.id.goBack)

        goBackButton.setOnClickListener {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        return receiptCreator
    }
}