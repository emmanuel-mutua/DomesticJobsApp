package com.lorraine.domesticjobs.presentation.employer.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import com.lorraine.domesticjobs.data.model.JobPosting
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddJobScreen(
    uiState: UiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onNameOfCountryChanged: (String) -> Unit,
    onNameOfCityChanged: (String) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    jobStartingDateUpdated: (ZonedDateTime) -> Unit,
    onBackPressed: () -> Unit,
    onSaveClicked: (JobPosting) -> Unit,
    onModeOfWorkChanged: (String) -> Unit,
    onSalaryUpdated: (String) -> Unit,
    onNumberOfEmployeesUpdated: (String) -> Unit,
    applicationDeadlineUpdated: (ZonedDateTime) -> Unit,
) {

    Scaffold(
        topBar = {
            WriteTopBar(
                selectedLog = uiState.selectedJobPosting,
                onDeleteConfirmed = onDeleteConfirmed,
                onBackPressed = onBackPressed,
                onDateTimeUpdated = onDateTimeUpdated,
            )
        },
        content = { paddingValues ->
            AddJobPostingContent(
                uiState = uiState,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                paddingValues = paddingValues,
                onSaveClicked = onSaveClicked,
                onModeOfWorkChanged = onModeOfWorkChanged,
                applicationDeadlineUpdated = applicationDeadlineUpdated,
                jobStartingDateUpdated = jobStartingDateUpdated,
                onNumberOfEmployeesUpdated = onNumberOfEmployeesUpdated,
                onNameOfCountryChanged = onNameOfCountryChanged,
                onSalaryUpdated = onSalaryUpdated,
                onNameOfCityChanged = onNameOfCityChanged
            )
        },
    )
}