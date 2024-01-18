package com.lorraine.hiremequick.presentation.jobseeker.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.hiremequick.connectivity.ConnectivityObserver
import com.lorraine.hiremequick.connectivity.NetworkConnectivityObserver
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.data.repository.JobPostings
import com.lorraine.hiremequick.data.repository.FirebaseJobPostingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class JobSeekerHomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
) : ViewModel() {
    private lateinit var allLogsJob: Job
    private lateinit var filteredLogsJob: Job

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""

    var jobPostings: MutableState<JobPostings> = mutableStateOf(RequestState.Idle)
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)
    var dateIsSelected by mutableStateOf(false)
        private set

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getAllJobs()
        }
        viewModelScope.launch {
            connectivity.observe().collect { network = it }
        }
    }

    fun getAllJobs(zonedDateTime: ZonedDateTime? = null) {
        dateIsSelected = zonedDateTime != null
        jobPostings.value = RequestState.Loading
        if (dateIsSelected && zonedDateTime != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                observeFilteredJobs(zonedDateTime = zonedDateTime)
            }
        } else {
            observeAllJobs()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAllJobs() {
        allLogsJob = viewModelScope.launch {
            if (::filteredLogsJob.isInitialized) {
                filteredLogsJob.cancelAndJoin()
            }
            FirebaseJobPostingRepo.getAllJobsPostings().debounce(2000).collect { result ->
                jobPostings.value = result
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeFilteredJobs(zonedDateTime: ZonedDateTime) {
        filteredLogsJob = viewModelScope.launch {
            if (::allLogsJob.isInitialized) {
                allLogsJob.cancelAndJoin()
            }
            FirebaseJobPostingRepo.getFilteredJobPostings(zonedDateTime = zonedDateTime, studentId = currentUserId)
                .collect { result ->
                    jobPostings.value = result
                }
        }
    }
}
