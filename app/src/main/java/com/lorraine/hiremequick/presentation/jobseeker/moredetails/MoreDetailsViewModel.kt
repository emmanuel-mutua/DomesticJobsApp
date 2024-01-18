package com.lorraine.hiremequick.presentation.jobseeker.moredetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.data.repository.FirebaseJobPostingRepo
import com.lorraine.hiremequick.utils.Contants.MOREDETAILS_SCREEN_ARGUMENT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var _uiState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        MutableStateFlow(UiState(selectedJob = JobPosting().apply { }))
    } else {
        TODO("VERSION.SDK_INT < O")
    }
        private set
    var uiState = _uiState.asStateFlow()


    private fun setSelectedJob(jobPosting: JobPosting) {
        _uiState.value = uiState.value.copy(selectedJob = jobPosting)
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

}

data class UiState(
    val selectedJob: JobPosting,
    val selectedJobId: String? = null,
    val isLoading: Boolean = false
)