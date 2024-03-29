package com.lorraine.domesticjobs.presentation.employer.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.domesticjobs.connectivity.ConnectivityObserver
import com.lorraine.domesticjobs.connectivity.NetworkConnectivityObserver
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.JobPostings
import com.lorraine.domesticjobs.data.repository.FirebaseJobPostingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class EmployerHomeViewModel @Inject constructor(
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
        getJobs()
        viewModelScope.launch {
            connectivity.observe().collect { network = it }
        }
    }

    fun getJobs(zonedDateTime: ZonedDateTime? = null) {
        dateIsSelected = zonedDateTime != null
        jobPostings.value = RequestState.Loading
        if (dateIsSelected && zonedDateTime != null) {
            getAllJobsFiltered(zonedDateTime = zonedDateTime)
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
            FirebaseJobPostingRepo.getAllJobsPostings(employerId = currentUserId).debounce(2000)
                .collectLatest { result ->
                    println(result)
                    jobPostings.value = result
                }
        }
    }

    private fun getAllJobsFiltered(zonedDateTime: ZonedDateTime) {
        filteredLogsJob = viewModelScope.launch {
            if (::allLogsJob.isInitialized) {
                allLogsJob.cancelAndJoin()
            }
            FirebaseJobPostingRepo.getFilteredJobPostings(
                zonedDateTime = zonedDateTime,
                studentId = currentUserId
            )
                .collect { result ->
                    jobPostings.value = result
                }
        }
    }
}
