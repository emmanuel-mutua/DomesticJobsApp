package com.johnstanley.attachmentapp.presentation.student.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private lateinit var allDiariesJob: Job
    private lateinit var filteredDiariesJob: Job

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
            observeFilteredDiaries(zonedDateTime = zonedDateTime)
        } else {
            observeAllDiaries()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeAllDiaries() {
        allDiariesJob = viewModelScope.launch {
            if (::filteredDiariesJob.isInitialized) {
                filteredDiariesJob.cancelAndJoin()
            }
            FirebaseAttachmentLogRepo.getAllAttachmentLogs().debounce(2000).collect { result ->
                attachmentLogs.value = result
            }
        }
    }

    private fun observeFilteredDiaries(zonedDateTime: ZonedDateTime) {
        filteredDiariesJob = viewModelScope.launch {
            if (::allDiariesJob.isInitialized) {
                allDiariesJob.cancelAndJoin()
            }
            FirebaseAttachmentLogRepo.getFilteredAttachmentLogs(zonedDateTime = zonedDateTime)
                .collect { result ->
                    attachmentLogs.value = result
                }
        }
    }
}
