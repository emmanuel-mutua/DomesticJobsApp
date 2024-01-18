package com.lorraine.hiremequick.presentation.jobseeker.moredetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.utils.Contants.MOREDETAILS_SCREEN_ARGUMENT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MoreDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    ): ViewModel() {
    var _uiState = MutableStateFlow(UiState())
        private set
    var uiState = _uiState.asStateFlow()
    init {
        getSelectedJobArgument()
    }
    private fun getSelectedJobArgument() {
        _uiState.update {
            it.copy(
                selectedJobId = savedStateHandle.get<JobPosting>(
                    key = MOREDETAILS_SCREEN_ARGUMENT_KEY,
                ),
            )
        }
    }
}
data class UiState(
    val selectedJobId: JobPosting? = null,
)