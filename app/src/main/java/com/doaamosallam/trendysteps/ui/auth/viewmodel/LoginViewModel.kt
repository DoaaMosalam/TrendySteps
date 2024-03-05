package com.doaamosallam.trendysteps.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doaamosallam.trendysteps.data.repository.UserPreferenceRepository
import kotlinx.coroutines.launch


class LoginViewModel(
    val userPrefs:UserPreferenceRepository
): ViewModel() {
    fun saveLoginState(isLoggedIn: Boolean) {
        viewModelScope.launch {


        }
    }
}