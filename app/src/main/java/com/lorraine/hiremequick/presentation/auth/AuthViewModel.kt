package com.lorraine.hiremequick.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.remote.ConnectivityMonitor.NetworkStatus
import com.lorraine.hiremequick.connectivity.ConnectivityObserver
import com.lorraine.hiremequick.connectivity.NetworkConnectivityObserver
import com.lorraine.hiremequick.data.Response
import com.lorraine.hiremequick.data.model.EmployerData
import com.lorraine.hiremequick.data.model.JobSeekerData
import com.lorraine.hiremequick.data.repository.AuthRepository
import com.lorraine.hiremequick.data.repository.JobApplicationRepoImpl
import com.lorraine.hiremequick.data.repository.StorageService
import com.lorraine.hiremequick.utils.Contants
import com.lorraine.hiremequick.utils.Contants.JobSeeker
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

    private var _registerState = MutableStateFlow(AuthStateData())
    val registerState = _registerState.asStateFlow()

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""
    private var _jobSeekerData = MutableStateFlow(JobSeekerData())
    private var _employerData = MutableStateFlow(EmployerData())

    private val _signInResponse = MutableStateFlow<Response<Boolean>>(Response.Idle)

    private var _signInEventResponse = Channel<SignInEventResponse>()
    val signInEventResponse = _signInEventResponse.receiveAsFlow()

    private val _signUpResponse = MutableStateFlow<Response<Boolean>>(Response.Idle)
    val signUpResponse = _signUpResponse.asStateFlow()

    private fun checkNetwork() {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collectLatest { status ->
                _registerState.update {
                    it.copy(
                        networkStatus = status
                    )
                }
            }
        }
    }

    fun signInEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _signInEventResponse.send(SignInEventResponse.Loading)
            val response = authRepo.signInEmailAndPassword(email, password)
            when (response) {
                Response.Loading -> {
                    _signInEventResponse.send(SignInEventResponse.Loading)
                }

                is Response.Failure -> {
                    _signInEventResponse.send(SignInEventResponse.Message(message = response.message))
                }

                is Response.Success -> {
                    _signInEventResponse.send(SignInEventResponse.Success)
                }

                Response.Idle -> Unit
            }
            _signInResponse.value = response
        }
    }

    private fun signUpEmailAndPassword(email: String, password: String) {
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
                        uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
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
                        uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    )
                }
                val response = storageService.addEmployer(_employerData.value)
                if (response) {
                }
            }
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
                uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
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

    private fun reloadUser() {
        viewModelScope.launch {
            authRepo.reloadFirebaseUser()
        }
    }
}

sealed class SignInEventResponse {
    data object Success : SignInEventResponse()
    data object Loading : SignInEventResponse()
    data class Message(val message: String) : SignInEventResponse()
}

data class AuthStateData(
    val isLoading: Boolean = false,
    val role: String = "",
    val message: String = "",
    val isSignedIn: Boolean = false,
    val networkStatus: ConnectivityObserver.Status = ConnectivityObserver.Status.Unavailable,
)

