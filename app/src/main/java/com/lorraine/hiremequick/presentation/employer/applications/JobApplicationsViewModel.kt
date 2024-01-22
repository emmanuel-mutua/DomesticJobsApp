package com.lorraine.hiremequick.presentation.employer.applications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.hiremequick.data.model.JobApplicationDetails
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.data.repository.JobApplicationRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobApplicationsViewModel @Inject constructor(

) : ViewModel() {
    private var _uiState = MutableStateFlow(JobApplicationsUiState())
    val uiState = _uiState.asStateFlow()

    val currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val employerId  = currentUser?.uid!!
    init {
        getJobApplications(employerId)
    }

    fun getJobApplications(employerId: String) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            Log.d("JobApplicationsViewModel", ": Getting data")
            val applications = JobApplicationRepoImpl.getJobApplications(employerId)
            when (applications) {
                is RequestState.Success -> {
                    applications.data.collectLatest { jobApplications ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                jobApplications = jobApplications
                            )
                        }
                    }
                }
                is RequestState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error Fetching applications"
                        )
                    }
                }
                RequestState.Loading -> {}
                RequestState.Idle -> {}
            }
        }
    }
    fun sendMessage(phoneNumber : String){

    }
    fun callApplicant(phoneNumber : String){

    }
    fun sendEmail(emailAddress : String){

    }
}

data class JobApplicationsUiState(
    val jobApplications: List<JobApplicationDetails> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)