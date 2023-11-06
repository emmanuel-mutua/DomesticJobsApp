package com.johnstanley.attachmentapp.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnstanley.attachmentapp.data.Response
import com.johnstanley.attachmentapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
) : ViewModel() {
    private var _registerState = MutableStateFlow(UserData())
    val registerState = _registerState.asStateFlow()
    val isEmailVerified = authRepo.isEmailVerified()
    var authState = authRepo.getAuthState(viewModelScope)
    val currentUser = authRepo.currentUser
    val email = _registerState.value.email
    val password = _registerState.value.password

    private val _signInResponse = MutableStateFlow<Response<Boolean>>(Response.FirstLaunch)
    val signInResponse = _signInResponse.asStateFlow()

    private val _signUpResponse = MutableStateFlow<Response<Boolean>>(Response.FirstLaunch)
    val signUpResponse = _signUpResponse.asStateFlow()

    fun signInEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _signInResponse.value = Response.Loading
            val response = authRepo.signInEmailAndPassword(email, password)
            _signInResponse.value = response
        }
    }

//    init {
//        getErrorMessage()
//    }

    fun getErrorMessage() {
        viewModelScope.launch {
            authRepo.getErrorMessage().collectLatest { errorMessage ->
                _registerState.update {
                    it.copy(
                        errorMsg = errorMessage,
                    )
                }
            }
        }
    }

    fun signUpEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _signUpResponse.value = Response.Loading
            _signUpResponse.value = authRepo.signUpEmailAndPassword(email, password)
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            authRepo.sendEmailVerification()
        }
    }

    fun signOut() {
        authRepo.signOut()
    }

    fun signUpUser(email: String, password: String) {
        signUpEmailAndPassword(email, password)
    }

    fun setRole(roleOption: String) {
        _registerState.update {
            it.copy(
                role = roleOption,
            )
        }
    }
}

data class UserData(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val registrationNumber: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isUserLoggedIn: Boolean = false,
    val goToStudentHomeScreen: Boolean = true,
    val isPassWordError: Boolean = false,
    val errorMsg: String = "",
)
