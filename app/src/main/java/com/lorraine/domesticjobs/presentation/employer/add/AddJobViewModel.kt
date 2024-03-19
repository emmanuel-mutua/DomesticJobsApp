package com.lorraine.domesticjobs.presentation.employer.add

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.FirebaseJobPostingRepo
import com.lorraine.domesticjobs.utils.Contants.ADD_SCREEN_ARGUMENT_KEY
import com.lorraine.domesticjobs.utils.toInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddLogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _uiState = MutableStateFlow(UiState())

    val uiState = _uiState.asStateFlow()

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init {
        getJobIdArgument()
        fetchSelectedJobPosting()
        _uiState.update {
            it.copy(
                employerId = currentUser?.uid ?: "",
                jobId = UUID.randomUUID().toString()
            )
        }
        val today = LocalDate.now()
        val disabledDates = mutableListOf<LocalDate>()
        var currentDateMin = today.minusDays(500)
        while (currentDateMin < today) {
            disabledDates.add(currentDateMin)
            currentDateMin = currentDateMin.plusDays(1)
        }
        _uiState.update {
            it.copy(
                disabledDates =disabledDates
            )
        }
    }

    private fun getJobIdArgument() {
        _uiState.update {
            it.copy(
                selectedJobPostingId = savedStateHandle.get<String>(
                    key = ADD_SCREEN_ARGUMENT_KEY,
                ),
            )
        }
    }

    private fun fetchSelectedJobPosting() {
        Log.d("Fetch selected", "fetchSelectedid : ${_uiState.value.selectedJobPostingId}")
        if (_uiState.value.selectedJobPostingId != null) {
            viewModelScope.launch {
                val result =
                    FirebaseJobPostingRepo.getSelectedJob(jobId = _uiState.value.selectedJobPostingId!!)
                when (result) {
                    is RequestState.Error -> {

                    }

                    RequestState.Idle -> {

                    }

                    RequestState.Loading -> {

                    }

                    is RequestState.Success -> {
                        Log.d("AddLogVm", "fetchSelectedAttachmentLog:${result.data.title} ")
                        setSelectedJob(jobPosting = result.data)
                        setTitle(title = result.data.title)
                        setDescription(description = result.data.description)
                        setModeOfWork(mode = result.data.modeOfWork)
                        setNumberOfEmployeesNeeded(number = result.data.numberOfEmployeesNeeded)
                    }
                }
            }
        }
    }

    private fun setSelectedJob(jobPosting: JobPosting) {
        _uiState.update {
            it.copy(
                selectedJobPosting = jobPosting,
            )
        }
    }

    fun setTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title,
            )
        }
    }

    fun setDescription(description: String) {
        _uiState.update {
            it.copy(
                description = description,
            )
        }
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        _uiState.update {
            it.copy(
                datePosted = zonedDateTime.toLocalDateTime().toInstant(),
            )
        }
    }


    fun upsertJob(
        jobPosting: JobPosting,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.selectedJobPostingId != null) {
                updateJobPosting(
                    jobPosting = jobPosting,
                    onSuccess = onSuccess,
                    onError = onError,
                )
            } else {
                insertJob(
                    jobPosting = jobPosting,
                    onSuccess = onSuccess,
                    onError = onError,
                )
            }
        }
    }

    fun updateJob(
        jobPosting: JobPosting,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            updateJobPosting(
                jobPosting = jobPosting,
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }

    private suspend fun insertJob(
        jobPosting: JobPosting,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val result =
            FirebaseJobPostingRepo.insertJob(
                jobPosting = jobPosting.apply {
                    if (_uiState.value.datePosted != null) {
                        datePosted = _uiState.value.datePosted?.toEpochMilli() ?: 0L
                    }
                },
            )
        if (result is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
                _uiState.update {
                    it.copy(
                        jobId = "",
                        title = "",
                        description = "",
                        modeOfWork = "",
                        numberOfEployeesNeeded = "",
                        nameOfCountry = "",
                        nameOfCity = ""
                    )
                }
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateJobPosting(
        jobPosting: JobPosting,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val result =
            FirebaseJobPostingRepo.updateJobPosting(
                jobPosting = jobPosting.apply {
                    jobId = _uiState.value.selectedJobPostingId!!
                    datePosted = if (_uiState.value.datePosted != null) {
                        _uiState.value.datePosted?.toEpochMilli() ?: 0L
                    } else {
                        _uiState.value.selectedJobPosting!!.datePosted
                    }
                },
            )
        if (result is RequestState.Success) {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        } else if (result is RequestState.Error) {
            withContext(Dispatchers.Main) {
                onError(result.error.message.toString())
            }
        }
    }

    fun deleteJobPosting(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.selectedJobPostingId != null) {
                val result =
                    FirebaseJobPostingRepo.deleteJobPosting(id = _uiState.value.selectedJobPostingId!!)
                if (result is RequestState.Success) {
                    withContext(Dispatchers.Main) {
                        uiState.value.selectedJobPosting?.let {
                        }
                        onSuccess()
                    }
                } else if (result is RequestState.Error) {
                    withContext(Dispatchers.Main) {
                        onError(result.error.message.toString())
                    }
                }
            }
        }
    }

    fun updateApplicationDeadlineDate(zonedDateTime: ZonedDateTime) {
        _uiState.update {
            it.copy(
                applicationDeadline = zonedDateTime.toLocalDateTime().toInstant(),
            )
        }
    }

    fun updateJobStartingDate(zonedDateTime: ZonedDateTime) {
        _uiState.update {
            it.copy(
                jobStartingDate = zonedDateTime.toLocalDateTime().toInstant(),
            )
        }
    }

    fun setModeOfWork(mode: String) {
        _uiState.update {
            it.copy(
                modeOfWork = mode,
            )
        }
    }

    fun setNumberOfEmployeesNeeded(number: String) {
        _uiState.update {
            it.copy(
                numberOfEployeesNeeded = number,
            )
        }
    }

    fun setNameOfCity(cityName: String) {
        _uiState.update {
            it.copy(
                nameOfCity = cityName,
            )
        }
    }

    fun setNameOfCountry(countryName: String) {
        _uiState.update {
            it.copy(
                nameOfCountry = countryName,
            )
        }
    }

    fun updateSalary(salary: String) {
        _uiState.update {
            it.copy(
                salary = salary,
            )
        }
    }
}

data class UiState(
    val jobId: String = "",
    val employerId: String = "",
    val selectedJobPostingId: String? = null,
    val selectedJobPosting: JobPosting? = null,
    val title: String = "",
    val description: String = "",
    val modeOfWork: String = "",
    val numberOfEployeesNeeded: String = "",
    val nameOfCountry: String = "",
    val nameOfCity: String = "",
    val datePosted: Instant? = null,
    val applicationDeadline: Instant? = null,
    val jobStartingDate: Instant? = null,
    val salary: String = "",
    val disabledDates : List<LocalDate> = emptyList()
)