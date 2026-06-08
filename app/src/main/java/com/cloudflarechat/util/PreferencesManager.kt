package com.cloudflarechat.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_NICKNAME = stringPreferencesKey("nickname")
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    val nickname: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_NICKNAME]
    }

    suspend fun saveAuth(token: String, userId: String, nickname: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_NICKNAME] = nickname
        }
    }

    suspend fun saveNickname(nickname: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NICKNAME] = nickname
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}