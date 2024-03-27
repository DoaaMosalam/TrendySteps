package com.doaamosallam.trendysteps.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doaamosallam.trendysteps.data.model.Resource
import com.doaamosallam.trendysteps.data.repository.auth.FirebaseAuthRepository
import com.doaamosallam.trendysteps.data.repository.user.UserPreferenceRepository
import com.doaamosallam.trendysteps.utils.isEmailValid
import com.doaamosallam.trendysteps.utils.isPasswordValid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
): ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState: SharedFlow<Resource<String>> = _loginState.asSharedFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isEmailValid() && password.isPasswordValid()
    }

    fun login() = viewModelScope.launch {
        val email = email.value
        val password = password.value
        if (isLoginIsValid.first()) {
            authRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
//                            userPrefs.saveUserEmail(email)
                        _loginState.emit(Resource.Success(resource.data ?: "Empty User Id"))
                    }

                    else -> _loginState.emit(resource)
                }
            }.launchIn(viewModelScope)
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val userPrefs: UserPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPrefs, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
//    val loginState: MutableStateFlow<Resource<String>?> = MutableStateFlow(null)
//
//   val email = MutableStateFlow("")
//    val password = MutableStateFlow("")
//
//    private val isLoginIsValid:Flow<Boolean> = combine(email, password) { email, password ->
//        email.isEmailValid() && password.isPasswordValid()
//    }
//   fun login() = viewModelScope.launch {
//       val email = email.value
//       val password = password.value
//       if(isLoginIsValid.first()){
//           authRepository.loginWithEmailAndPassword(email,password).onEach { resource->
//               when(resource){
//                   is Resource.Loading -> loginState.update { Resource.Loading() }
//                   is Resource.Success -> {
////                          userPrefs.saveUserEmail(email)
//                          loginState.update { Resource.Success(resource.data?:"Empty user ID") }
//                   }
//                   is Resource.Error -> loginState.value =
//                       Resource.Error(resource.exception?: Exception ( "An error occurred"))
//               }
//           }.launchIn(viewModelScope)
//       }else{
//           loginState.value = Resource.Error(Exception("Invalid email or password"))
//       }
//   }
//
//
//
//
//    companion object {
//        private const val TAG = "LoginViewModel"
//    }
//
//}
//
//
//// create viewmodel factory class
//class LoginViewModelFactory(
//    private val userPrefs: UserPreferenceRepository,
//    private val authRepository: FirebaseAuthRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPrefs, authRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}


