package com.lorraine.hiremequick.presentation.employer.applications

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.hiremequick.data.model.JobApplicationDetails
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.data.repository.JobApplicationRepo
import com.lorraine.hiremequick.data.repository.JobApplicationRepoImpl
import com.lorraine.hiremequick.presentation.jobseeker.applications.JobSeekerApplicationsUiState
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
    private var _uiState = MutableStateFlow(JobSeekerApplicationsUiState())
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
    fun sendMessage(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        context.startActivity(Intent.createChooser(intent, "Choose a messaging app"))
    }

    fun callApplicant(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(Intent.createChooser(intent, "Choose a calling app"))
    }

    fun sendEmail(context: Context, emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$emailAddress")
        context.startActivity(Intent.createChooser(intent, "Send email"))
    }

    fun acceptJobSeeker(applicantId: String) {
        viewModelScope.launch {
            JobApplicationRepoImpl.acceptJobSeeker(applicantId)
        }
    }
    fun declineJobSeeker(applicantId: String) {
        viewModelScope.launch {
            JobApplicationRepoImpl.declineJobSeeker(applicantId)
        }
    }

}

data class JobApplicationsUiState(
    val jobApplications: List<JobApplicationDetails> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)