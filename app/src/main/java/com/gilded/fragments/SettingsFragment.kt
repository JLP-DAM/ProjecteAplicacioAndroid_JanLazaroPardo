package com.gilded.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.gilded.R
import java.util.Currency


class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingsFragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        val pickCurrencyButton: Button = settingsFragmentView.findViewById(R.id.pickCurrency)
        val currenciesConstraintLayout: ConstraintLayout = settingsFragmentView.findViewById(R.id.currencies)
        val currenciesScrollView: ScrollView = currenciesConstraintLayout.findViewById(R.id.currenciesScrollView)
        val currenciesLinearLayout: LinearLayout = currenciesScrollView.findViewById(R.id.currenciesLinearLayout)
        val closeDialogImageButton: ImageButton = settingsFragmentView.findViewById(R.id.closeDialog)

        val closeButton: ImageButton = settingsFragmentView.findViewById(R.id.close)

        for (currency in Currency.getAvailableCurrencies()) {
            val currencyCardView: View = inflater.inflate(R.layout.currency, null)
            val categoryNameTextView: TextView = currencyCardView.findViewById(R.id.currency)

            categoryNameTextView.text = "${currency.symbol} - ${currency.displayName}"

            currenciesLinearLayout.addView(currencyCardView)

            currencyCardView.setOnClickListener {
                currenciesConstraintLayout.visibility = View.GONE
            }
        }


        pickCurrencyButton.setOnClickListener {
            currenciesConstraintLayout.visibility = View.VISIBLE
        }

        closeDialogImageButton.setOnClickListener {
            currenciesConstraintLayout.visibility = View.GONE
        }

        closeButton.setOnClickListener {
            val homeFragment = HomeFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, homeFragment)
                ?.commit()
        }

        return settingsFragmentView
    }
}