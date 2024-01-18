package com.lorraine.hiremequick.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.hiremequick.data.Response
import com.lorraine.hiremequick.data.repository.AuthRepository
import com.lorraine.hiremequick.data.repository.StorageService
import com.lorraine.hiremequick.utils.Contants
import com.lorraine.hiremequick.utils.Contants.JobSeeker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val storageService: StorageService,
) : ViewModel() {
    private var _registerState = MutableStateFlow(AuthStateData())
    val registerState = _registerState.asStateFlow()
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""
    private var _jobSeekerData = MutableStateFlow(JobSeekerData())
    private var _employerData = MutableStateFlow(EmployerData())

    private val _signInResponse = MutableStateFlow<Response<Boolean>>(Response.Idle)
    val signInResponse = _signInResponse.asStateFlow()

    private val _signUpResponse = MutableStateFlow<Response<Boolean>>(Response.Idle)
    val signUpResponse = _signUpResponse.asStateFlow()

    fun signInEmailAndPassword(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _signInResponse.value = Response.Loading
            val response = authRepo.signInEmailAndPassword(email, password)
            when (response) {
                Response.Loading -> {
                    _registerState.update {
                        it.copy(
                            isLoading = true,
                        )
                    }
                }

                is Response.Failure -> {
                    _registerState.update {
                        it.copy(
                            isLoading = false,
                            message = response.message,
                        )
                    }
                }

                is Response.Success -> {
                        _registerState.update {
                            it.copy(
                                isLoading = false,
                                isSignedIn = true,
                                message = "Success"
                            )
                        }
                }

                Response.Idle -> Unit
            }
            _signInResponse.value = response
        }
        onSuccess()
    }

    fun signUpEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _signUpResponse.value = Response.Loading
            val response = authRepo.signUpEmailAndPassword(email, password)
            delay(3000)
            _signUpResponse.value = response
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
        phoneNumber: String,
        email: String,
    ) {
        val role = _registerState.value.role
        if (role == JobSeeker) {
            _jobSeekerData.update {
                it.copy(
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    email = email,
                    role = role,
                )
            }
        } else {
            _employerData.update {
                it.copy(
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    email = email,
                    role = role,
                )
            }
        }
    }

    fun saveUserToDataBase() {
        val role = _registerState.value.role
        if (role == Contants.JobSeeker) {
            viewModelScope.launch {
                delay(2000)
                _jobSeekerData.update {
                    it.copy(
                        uid = FirebaseAuth.getInstance()?.currentUser?.uid ?: "",
                    )
                }
                val response = storageService.addJobSeeker(_jobSeekerData.value)
                if (response) {
                }
            }
        } else {
            viewModelScope.launch {
                _employerData.update {
                    it.copy(
                        uid = FirebaseAuth.getInstance()?.currentUser?.uid ?: "",
                    )
                }
                val response = storageService.addEmployer(_employerData.value)
                if (response) {
                }
            }
        }
    }

    fun setRegNo(registrationNumber: String) {
        _jobSeekerData.update {
            it.copy(
                registrationNumber = registrationNumber,
            )
        }
    }

    fun getRoleFromUserData(onDataLoaded: (String) -> Unit) {
        _registerState.update {
            it.copy(
                isLoading = true,
            )
        }
        viewModelScope.launch {
            storageService.getUserData(
                uid = FirebaseAuth.getInstance()?.currentUser?.uid ?: "",
                onSuccess = { document ->
                    Log.d("VM", document.id)
                    Log.d("VM", currentUserId)
                    val userRole = document.getString("role") ?: ""
                    Log.d("VM", userRole)
                    _registerState.update {
                        it.copy(
                            role = userRole,
                            isLoading = false,
                        )
                    }
                    onDataLoaded(userRole)
                },
            )
        }
    }

    fun reloadUser() {
        viewModelScope.launch {
            authRepo.reloadFirebaseUser()
        }
    }
}

data class AuthStateData(
    val isLoading: Boolean = false,
    val role: String = "",
    val message: String = "",
    val isSignedIn: Boolean = false,
)

data class JobSeekerData(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val registrationNumber: String = "",
    val phoneNumber: String = "",
    val role: String = "",
)

data class EmployerData(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "",
)
