package com.example.timemanager.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timemanager.repository.UserPreferencesRepository.Companion.DATA_STORE_NAME
import com.example.timemanager.repository.UserPreferencesRepository.PreferencesKeys.SHEET_SELECTED
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

    suspend fun clearPreferences() {
        context.dataStore.edit {
            it.clear()
        }
    }

    private object PreferencesKeys {
        val SHEET_SELECTED = intPreferencesKey(SHEET_SELECTED_KEY)
    }

    companion object {
        const val DATA_STORE_NAME = "preferences"
        private const val SHEET_SELECTED_KEY = "project_selected"
    }
}
