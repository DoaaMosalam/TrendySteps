package com.doaamosallam.trendysteps.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    suspend fun isUserLoggedIn(): Flow<Boolean>
    suspend fun saveLoginState(isLoggedIn: Boolean)
    suspend fun saveUserID(userId: String)
}