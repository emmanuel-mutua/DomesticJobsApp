package com.lorraine.domesticjobs.presentation.jobseeker.moredetails

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.domesticjobs.data.model.JobApplicationDetails
import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.FirebaseJobPostingRepo
import com.lorraine.domesticjobs.data.repository.JobApplicationRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreDetailsViewModel @Inject constructor(
) : ViewModel() {
    private val currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var _uiState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        MutableStateFlow(UiState(selectedJob = JobPosting().apply { }))
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    val uiState = _uiState.asStateFlow()


    private var _jobApplicationDetails = MutableStateFlow(JobApplicationDetails())
    val jobApplicationDetails = _jobApplicationDetails.asStateFlow()
    init {
        _jobApplicationDetails.update {
            it.copy(
                applicantId = currentUser?.uid?:"applicant id null"
            )
        }
    }
    fun getSelectedJob(jobPostingId: String?) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            val result =
                FirebaseJobPostingRepo.getSelectedJob(jobId = jobPostingId!!)
            when (result) {
                is RequestState.Error -> {

                }

                RequestState.Idle -> {

                }

                RequestState.Loading -> {

                }

                is RequestState.Success -> {
                    Log.d("AddLogVm", "fetchSelectedAttachmentLog:${result.data.title} ")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedJob = result.data
                        )
                    }
                }
            }
        }
    }

    fun setApplicantName(applicantName: String) {
        _jobApplicationDetails.update {
            it.copy(
                applicantName = applicantName
            )
        }
    }

    fun setApplicantEmail(applicantEmail: String) {
        _jobApplicationDetails.update {
            it.copy(
                applicantEmail = applicantEmail
            )
        }
    }

    fun setApplicantPhoneNumber(applicantPhoneNumber: String) {
        _jobApplicationDetails.update {
            it.copy(
                applicantPhoneNumber = applicantPhoneNumber
            )
        }
    }

    fun setApplicantExperienceDescription(experienceDescription: String) {
        _jobApplicationDetails.update {
            it.copy(
                experienceDescription = experienceDescription
            )
        }
    }

     fun setEmployerIdAndJobIdAndTitle(employerId: String,selectedJobId :String,jobTitle : String) {
        _jobApplicationDetails.update {
            it.copy(
                employerId = employerId,
                selectedJobId = selectedJobId,
                jobTitle = jobTitle
            )
        }
    }

    fun sendApplicationDetails(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val request = JobApplicationRepoImpl.insertJobApplication(_jobApplicationDetails.value)
            when(request){
                is RequestState.Success -> {
                    Log.d("MoreDetailsVm", "sendApplicationDetails: ${_jobApplicationDetails.value.selectedJobId}")
                    _jobApplicationDetails.update {
                        it.copy(
                            applicantName = "",
                            applicantEmail = "",
                        )
                    }
                    onSuccess()
                }
                is RequestState.Error -> {
                    onError("Error occurred")
                }
                RequestState.Idle -> Unit
                RequestState.Loading -> {

                }
            }
        }
    }

    fun updateApplicants(jobId:String){
        viewModelScope.launch {
            FirebaseJobPostingRepo.addApplicantIdToJobPosting(
                jobId = jobId,
                applicantId = currentUser?.uid?:"new applicant"
            )
        }
    }

}


data class UiState(
    val selectedJob: JobPosting,
    val selectedJobId: String? = null,
    val isLoading: Boolean = false
)