package com.example.csc490group3.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.FileOutputStream
import java.io.PrintWriter

class AppStorage ( private val context: Context) {
    companion object{
        private val Context.dataStore by
        preferencesDataStore(name = "app_preferences")

        private object PreferencesKeys {
            val DARK_MODE = booleanPreferencesKey("dark_mode")
        }
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    suspend fun saveDarkMode(darkmode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = darkmode
        }
    }


}