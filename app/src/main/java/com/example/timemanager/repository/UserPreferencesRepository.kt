package com.example.timemanager.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanager.repository.UserPreferencesRepository.Companion.DATA_STORE_NAME
import com.example.timemanager.repository.UserPreferencesRepository.PreferencesKeys.SHEET_SELECTED
import com.example.timemanager.repository.UserPreferencesRepository.PreferencesKeys.SORT_INFO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun sheetSelected() = context.dataStore.data.map { preferences ->
        preferences[SHEET_SELECTED] ?: 1
    }

    suspend fun setSheetSelected(sheetSelected: Int) {
        context.dataStore.edit { preferences ->
            preferences[SHEET_SELECTED] = sheetSelected
        }
    }

    fun sortInfo() = context.dataStore.data.map { preferences ->
        preferences[SORT_INFO] ?: ""
    }

    suspend fun setSortInfo(sortInfo : String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_INFO] = sortInfo
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit {
            it.clear()
        }
    }

    private object PreferencesKeys {
        val SHEET_SELECTED = intPreferencesKey(SHEET_SELECTED_KEY)
        val SORT_INFO = stringPreferencesKey(SORT_INFO_KEY)
    }

    companion object {
        const val DATA_STORE_NAME = "preferences"
        private const val SHEET_SELECTED_KEY = "sheet_selected"
        private const val SORT_INFO_KEY = "sort_info"
    }
}
