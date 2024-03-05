package com.doaamosallam.trendysteps.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.doaamosallam.trendysteps.data.local.DataStore.IS_USER_LOGGED_IN
import com.doaamosallam.trendysteps.data.local.DataStore.USER_ID

import com.doaamosallam.trendysteps.data.local.dataStore
import dagger.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class UserDataStoreRepositoryImpl(private var context: Context): UserPreferenceRepository {

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preference ->
            preference[IS_USER_LOGGED_IN] ?: false
        }
    }
    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit{preferences ->
            preferences[IS_USER_LOGGED_IN] = isLoggedIn
        }
    }
    override suspend fun saveUserID(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }
}