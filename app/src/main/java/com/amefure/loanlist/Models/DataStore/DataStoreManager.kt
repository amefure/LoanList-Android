package com.amefure.loanlist.Models.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(private val context: Context) {


    companion object {
        val SORT_ITEM = stringPreferencesKey("sort_item")
        val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        val CURRENT_USER_NAME = stringPreferencesKey("current_user_name")
    }

    suspend fun saveSortItem(sortItem: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[SORT_ITEM] = sortItem
            }
        } catch (e: IOException) {
            // Handle the exception here if needed
        }
    }

    public fun observeSortItem(): Flow<String?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[SORT_ITEM]
        }
    }

    suspend fun saveCurrentUserId(id: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[CURRENT_USER_ID] = id
            }
        } catch (e: IOException) {
            // Handle the exception here if needed
        }
    }

   public fun observeCurrentUserId(): Flow<Int?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[CURRENT_USER_ID]
        }
    }

    suspend fun saveCurrentUserName(name: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[CURRENT_USER_NAME] = name
            }
        } catch (e: IOException) {
            // Handle the exception here if needed
        }
    }
    public fun observeCurrentUserName(): Flow<String?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[CURRENT_USER_NAME]
        }
    }
}
