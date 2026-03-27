package com.gilded.services

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
}