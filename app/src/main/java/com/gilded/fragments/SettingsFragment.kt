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
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gilded.R
import com.gilded.services.PreferencesKeys
import com.gilded.services.preferencesDataStore
import com.gilded.viewmodels.SettingsViewModel
import com.gilded.viewmodels.UsageDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Currency
import kotlin.getValue


class SettingsFragment : Fragment() {
    private val usageDataViewModel: UsageDataViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val settingsFragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        val pickCurrencyButton: Button = settingsFragmentView.findViewById(R.id.pickCurrency)
        val currenciesConstraintLayout: ConstraintLayout = settingsFragmentView.findViewById(R.id.currencies)
        val currenciesScrollView: ScrollView = currenciesConstraintLayout.findViewById(R.id.currenciesScrollView)
        val currenciesLinearLayout: LinearLayout = currenciesScrollView.findViewById(R.id.currenciesLinearLayout)
        val usageDataButton: Button = settingsFragmentView.findViewById(R.id.usageData)
        val closeDialogImageButton: ImageButton = settingsFragmentView.findViewById(R.id.closeDialog)

        val closeButton: ImageButton = settingsFragmentView.findViewById(R.id.close)

        val voiceNavigationSwitch: SwitchCompat = settingsFragmentView.findViewById(R.id.voiceNavigation)

        voiceNavigationSwitch.isChecked = settingsViewModel.voiceNavigation.value ?: false

        voiceNavigationSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setVoiceNavigation(isChecked)
        }

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

        closeDialogImageButton.setOnClickListener {
            currenciesConstraintLayout.visibility = View.GONE
        }

        usageDataButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainerView, UsageDataFragment())
                ?.commit()
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