package com.doaamosallam.trendysteps.data.repository.auth

import com.doaamosallam.trendysteps.data.model.Resource
import kotlinx.coroutines.flow.Flow


interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Resource<String>>
}