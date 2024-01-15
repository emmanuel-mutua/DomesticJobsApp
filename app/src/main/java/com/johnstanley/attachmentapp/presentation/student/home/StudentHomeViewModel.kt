package com.johnstanley.attachmentapp.presentation.student.home

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
import com.johnstanley.attachmentapp.connectivity.ConnectivityObserver
import com.johnstanley.attachmentapp.connectivity.NetworkConnectivityObserver
import com.johnstanley.attachmentapp.data.model.RequestState
import com.johnstanley.attachmentapp.data.repository.AttachmentLogs
import com.johnstanley.attachmentapp.data.repository.FirebaseAttachmentLogRepo
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
class StudentHomeViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
) : ViewModel() {
    private lateinit var allLogsJob: Job
    private lateinit var filteredLogsJob: Job

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""

    var attachmentLogs: MutableState<AttachmentLogs> = mutableStateOf(RequestState.Idle)
    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)
    var dateIsSelected by mutableStateOf(false)
        private set

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getAttachmentLogs()
        }
        viewModelScope.launch {
            connectivity.observe().collect { network = it }
        }
    }

    fun getAttachmentLogs(zonedDateTime: ZonedDateTime? = null) {
        dateIsSelected = zonedDateTime != null
        attachmentLogs.value = RequestState.Loading
        if (dateIsSelected && zonedDateTime != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                observeFilteredAttachmentLogs(zonedDateTime = zonedDateTime)
            }
        } else {
            observeAllAttachmentLogs()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAllAttachmentLogs() {
        allLogsJob = viewModelScope.launch {
            if (::filteredLogsJob.isInitialized) {
                filteredLogsJob.cancelAndJoin()
            }
            FirebaseAttachmentLogRepo.getAllAttachmentLogs(studentId = currentUserId).debounce(2000).collect { result ->
                attachmentLogs.value = result
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeFilteredAttachmentLogs(zonedDateTime: ZonedDateTime) {
        filteredLogsJob = viewModelScope.launch {
            if (::allLogsJob.isInitialized) {
                allLogsJob.cancelAndJoin()
            }
            FirebaseAttachmentLogRepo.getFilteredAttachmentLogs(zonedDateTime = zonedDateTime, studentId = currentUserId)
                .collect { result ->
                    attachmentLogs.value = result
                }
        }
    }
}
