package com.amefure.loanlist.Models.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(private val context: Context) {


    companion object {
        val SORT_ITEM = stringPreferencesKey("sort_item")
        val CURRENT_USER = stringPreferencesKey("current_user")
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

    suspend fun saveCurrentUser(currentUser: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[CURRENT_USER] = currentUser
            }
        } catch (e: IOException) {
            // Handle the exception here if needed
        }
    }

   public fun observeCurrentUser(): Flow<String?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[CURRENT_USER]
        }
    }
}
