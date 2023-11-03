package com.johnstanley.attachmentapp.presentation.auth

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {
    private var _registerState = MutableStateFlow(UserData())
    val registerState = _registerState.asStateFlow()

    fun setFullName(fullName: String) {
        _registerState.update {
            it.copy(
                fullName = fullName,
            )
        }
    }

    fun setRegNoName(registrationNumber: String) {
        _registerState.update {
            it.copy(
                registrationNumber = registrationNumber,
            )
        }
    }
    fun setEmail(email: String) {
        _registerState.update {
            it.copy(
                email = email,
            )
        }
    }
    fun setPhoneNumber(phoneNumber: String) {
        _registerState.update {
            it.copy(
                phoneNumber = phoneNumber,
            )
        }
    }

    fun setPassword(password: String) {
        _registerState.update {
            it.copy(
                password = password,
            )
        }
    }

    fun registerUser() {
        return
    }

    fun setConfirmPassword(confirmPassword: String) {
        _registerState.update {
            it.copy(
                confirmPassword = confirmPassword,
            )
        }
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
    var isLoading: Boolean = false,
    var fullName: String = "",
    var registrationNumber: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var role: String = "",
    var password: String = "",
    var confirmPassword: String = "",
)
