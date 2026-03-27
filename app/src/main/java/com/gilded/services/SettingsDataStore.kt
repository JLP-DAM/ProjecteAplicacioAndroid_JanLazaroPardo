package com.gilded.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gilded.services.PreferencesKeys.CURRENCY_SYMBOL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "Settings"
)

object SettingsDataStore {
    fun getCurrencySymbol(context: Context): Flow<String> {
        return context.preferencesDataStore.data.map { preferences ->
            preferences[CURRENCY_SYMBOL] ?: "€"
        }
    }
}