package com.lorraine.domesticjobs.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.domesticjobs.connectivity.ConnectivityObserver
import com.lorraine.domesticjobs.connectivity.NetworkConnectivityObserver
import com.lorraine.domesticjobs.data.Response
import com.lorraine.domesticjobs.data.model.EmployerData
import com.lorraine.domesticjobs.data.model.JobSeekerData
import com.lorraine.domesticjobs.data.repository.AuthRepository
import com.lorraine.domesticjobs.data.repository.StorageService
import com.lorraine.domesticjobs.utils.Contants
import com.lorraine.domesticjobs.utils.Contants.JobSeeker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val storageService: StorageService,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    init {
        reloadUser()
        checkNetwork()
    }

    private var _authState = MutableStateFlow(AuthStateData())
    val authState = _authState.asStateFlow()

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""
    private var _jobSeekerData = MutableStateFlow(JobSeekerData())
    private var _employerData = MutableStateFlow(EmployerData())

    private val _signInResponse = MutableStateFlow<Response<Boolean>>(Response.Idle)

    private var _authEventResponse = Channel<AuthEventResponse>()
    val authEventResponse = _authEventResponse.receiveAsFlow()

    private fun checkNetwork() {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collectLatest { status ->
                _authState.update {
                    it.copy(
                        networkStatus = status
                    )
                }
            }
        }
    }

    fun signInEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            updateLoading(true)
            val response = authRepo.signInEmailAndPassword(email, password)
            when (response) {

                is Response.Failure -> {
                    _authEventResponse.send(AuthEventResponse.Failure(message = response.message))
                }

                is Response.Success -> {
                    _authEventResponse.send(AuthEventResponse.Success)
                }

                Response.Idle -> Unit
            }
            _signInResponse.value = response
        }
        updateLoading(false)
    }

    private fun signUpEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            //update state to loading
            val response = authRepo.signUpEmailAndPassword(email, password)
            delay(3000)
            when (response) {
                is Response.Failure -> {
                    onAuthError(response.message)
                }

                Response.Idle -> {

                }

                is Response.Success -> {
                    saveUserToDataBase()
                }

            }
        }
    }

    fun signOut() {
        authRepo.signOut()
    }


    fun setRole(roleOption: String) {
        _authState.update {
            it.copy(
                role = roleOption,
            )
        }
    }

    fun saveUserDetails(
        fullName: String,
        phoneNumber: String,
        email: String,
        password: String,
    ) {
        updateLoading(true)
        val role = _authState.value.role
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
        signUpEmailAndPassword(email, password)
    }

    private fun saveUserToDataBase() {
        val role = _authState.value.role
        if (role == Contants.JobSeeker) {
            viewModelScope.launch {
                delay(2000)
                _jobSeekerData.update {
                    it.copy(
                        uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    )
                }
                val response = storageService.addJobSeeker(_jobSeekerData.value)
                when (response) {
                    true -> {
                        onAuthSuccess()
                    }

                    false -> {
                        onAuthError("Error during registration, try again")
                    }
                }
            }
        } else {
            viewModelScope.launch {
                _employerData.update {
                    it.copy(
                        uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    )
                }
                val response = storageService.addEmployer(_employerData.value)
                when (response) {
                    true -> {
                        onAuthSuccess()
                    }

                    false -> {
                        onAuthError("Unknown error occurred, try again")
                    }
                }
            }
        }
    }

    private fun onAuthSuccess() {
        updateLoading(false)
        viewModelScope.launch {
            _authEventResponse.send(AuthEventResponse.Success)
        }
    }

    private fun onAuthError(errorMessage: String) {
        updateLoading(false)
        viewModelScope.launch {
            _authEventResponse.send(AuthEventResponse.Failure(errorMessage))
        }
    }

    fun getRoleFromUserData(onDataLoaded: (String) -> Unit) {
        _authState.update {
            it.copy(
                isLoading = true,
            )
        }
        viewModelScope.launch {
            storageService.getUserData(
                uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                onSuccess = { document ->
                    Log.d("VM", document.id)
                    Log.d("VM", currentUserId)
                    val userRole = document.getString("role") ?: ""
                    Log.d("VM", userRole)
                    _authState.update {
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

    private fun reloadUser() {
        viewModelScope.launch {
            authRepo.reloadFirebaseUser()
        }
    }

    fun resetPassword(email: String): Boolean {
        viewModelScope.launch {
            val result = authRepo.resetPassword(email)
            _authState.update {
                it.copy(
                    resetPassResult = result
                )
            }
        }
        return _authState.value.resetPassResult
    }
    fun updateLoading(loading : Boolean){
        _authState.update {
            it.copy(
                isLoading = loading
            )
        }
    }
}

sealed class AuthEventResponse {
    data object Success : AuthEventResponse()
    data class Failure(val message: String) : AuthEventResponse()
}

data class AuthStateData(
    val isLoading: Boolean = false,
    val role: String = "",
    val resetPassResult: Boolean = false,
    val networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.Unavailable,
)

