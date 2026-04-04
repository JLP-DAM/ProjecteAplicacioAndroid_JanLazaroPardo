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
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.services.PreferencesKeys
import com.gilded.services.UsageData
import com.gilded.services.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        val emissionsTextView: TextView = settingsFragmentView.findViewById(R.id.emissions)

        for (currency in Currency.getAvailableCurrencies()) {
            val currencyCardView: View = inflater.inflate(R.layout.currency, null)
            val categoryNameTextView: TextView = currencyCardView.findViewById(R.id.currency)

            categoryNameTextView.text = "${currency.symbol} - ${currency.displayName}"

            currenciesLinearLayout.addView(currencyCardView)

            currencyCardView.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    requireContext().preferencesDataStore.edit { preferences ->
                        preferences[PreferencesKeys.CURRENCY_SYMBOL] = currency.symbol
                    }
                }

                currenciesConstraintLayout.visibility = View.GONE
            }
        }


        pickCurrencyButton.setOnClickListener {
            currenciesConstraintLayout.visibility = View.VISIBLE
        }

        // he trobat aquesta estatistica online en varis articles
        // deien que són 63 KG de Co2 per Any si s'utilitza el mobil una hora al dia
        // 63 / 365 = 0.17260273972
        val co2KGPerHour = 0.17260273972
        var usageHours = UsageData.usageTime / (1000 * 60 * 60)

        emissionsTextView.setText("Emissions de Co2: " + String.format("%.2f", usageHours * co2KGPerHour) + " KG " + usageHours)

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