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
fun UpdateJobScreen(
    uiState: UiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onModeOfWorkChanged: (String) -> Unit,
    onNumberOfEmployeesUpdated: (String) -> Unit,
    onNameOfCountryChanged: (String) -> Unit,
    onNameOfCityChanged: (String) -> Unit,
    applicationDeadlineUpdated: (ZonedDateTime) -> Unit,
    jobStartingDateUpdated: (ZonedDateTime) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onBackPressed: () -> Unit,
    onSalaryUpdated: (String) -> Unit,
    onSaveClicked: (JobPosting) -> Unit,
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
                onModeOfWorkChanged = onModeOfWorkChanged,
                onNumberOfEmployeesUpdated = onNumberOfEmployeesUpdated,
                applicationDeadlineUpdated = applicationDeadlineUpdated,
                jobStartingDateUpdated = jobStartingDateUpdated,
                paddingValues = paddingValues,
                onSaveClicked = onSaveClicked,
                onNameOfCountryChanged = onNameOfCountryChanged,
                onSalaryUpdated = onSalaryUpdated,
                onNameOfCityChanged = onNameOfCityChanged,
                )
        },
    )
}
