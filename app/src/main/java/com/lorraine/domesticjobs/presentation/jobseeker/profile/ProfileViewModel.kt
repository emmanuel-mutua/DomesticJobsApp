package com.lorraine.domesticjobs.presentation.jobseeker.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lorraine.domesticjobs.data.model.JobSeekerData
import com.lorraine.domesticjobs.data.repository.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val storageService: StorageService
) : ViewModel() {
    private var _jobSeekerUiState  = MutableStateFlow(JobSeekerData())
    val jobSeekerUiState = _jobSeekerUiState.asStateFlow()

    init {
        getRoleFromUserData()
    }
    private fun getRoleFromUserData() {
        viewModelScope.launch {
            storageService.getUserData(
                uid = FirebaseAuth.getInstance()?.currentUser?.uid ?: "",
                onSuccess = { document ->
                    val fullName = document.getString("fullName") ?: ""
                    val email = document.getString("email") ?: ""
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    _jobSeekerUiState.update {
                        it.copy(
                            fullName = fullName,
                            email = email,
                            phoneNumber = phoneNumber
                        )
                    }
                },
            )
        }
    }

}