package com.gilded.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gilded.services.PreferencesKeys.CURRENCY_SYMBOL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.collections.get

val Context.usageDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "Usage"
)

object UsageDataStore {
    fun getFragment(context: Context, fragmentName: String): Flow<Int> {
        return context.usageDataStore.data.map { preferences ->
            (preferences[intPreferencesKey(fragmentName)] ?: 0)
        }
    }

    suspend fun incrementFragment(context: Context, fragmentName: String) {
        context.usageDataStore.edit {
            it[intPreferencesKey(fragmentName)] = (it[intPreferencesKey(fragmentName)] ?: 0) + 1
        }
    }

    suspend fun getAllKeys(context: Context): Set<Preferences.Key<*>>? {
        val keys = context.usageDataStore.data
            .map {
                it.asMap().keys
            }
        return keys.firstOrNull()
    }
}