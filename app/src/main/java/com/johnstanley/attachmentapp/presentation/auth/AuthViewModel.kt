package com.johnstanley.attachmentapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnstanley.attachmentapp.data.Response
import com.johnstanley.attachmentapp.data.repository.AuthRepository
import com.johnstanley.attachmentapp.utils.Contants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
) : ViewModel() {
    private var _registerState = MutableStateFlow(AuthStateData())
    val registerState = _registerState.asStateFlow()
    val isEmailVerified = authRepo.isEmailVerified()
    var authState = authRepo.getAuthState(viewModelScope)
    val currentUser = authRepo.currentUser
    val currentUserId = currentUser?.uid.toString()
    private var _studentData = MutableStateFlow(StudentData())
    private var _staffData = MutableStateFlow(StaffData())

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

    fun saveUserDetails(
        fullName: String,
        registrationNumber: String,
        phoneNumber: String,
        email: String,
        role: String,
    ) {
        if (role == Contants.StudentText) {
            _studentData.update {
                it.copy(
                    fullName = fullName,
                    registrationNumber = registrationNumber,
                    phoneNumber = phoneNumber,
                    email = email,
                    role = role,
                )
            }
            _registerState.update {
                it.copy(
                    role = role,
                )
            }
        } else {
            _staffData.update {
                it.copy(
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    email = email,
                    role = role,
                )
            }
            _registerState.update {
                it.copy(
                    role = role,
                )
            }
        }
    }

    fun saveUserToDataBase() {
        val role = _registerState.value.role
        if (role == Contants.StudentText) {
        } else {
        }
    }
}

data class AuthStateData(
    val isLoading: Boolean = false,
    val role: String = "",
    val isUserLoggedIn: Boolean = false,
    val goToStudentHomeScreen: Boolean = true,
)

data class StudentData(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val registrationNumber: String = "",
    val phoneNumber: String = "",
    val role: String = "",
)

data class StaffData(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "",
)
