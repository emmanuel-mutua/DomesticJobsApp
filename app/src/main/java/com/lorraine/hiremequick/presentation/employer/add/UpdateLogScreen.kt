package com.lorraine.hiremequick.presentation.employer.add


import android.annotation.SuppressLint
import android.content.pm.ChangedPackages
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import com.lorraine.hiremequick.data.model.JobPosting
import java.time.ZonedDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateJobScreen(
    uiState: UiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onModeOfWorkChanged: (String) -> Unit,
    onNumberOfEmployeesUpdated: (String) -> Unit,
    applicationDeadlineUpdated: (ZonedDateTime) -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onBackPressed: () -> Unit,
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
                paddingValues = paddingValues,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onModeOfWorkChanged = onModeOfWorkChanged,
                applicationDeadlineUpdated = applicationDeadlineUpdated,
                onNumberOfEmployeesUpdated = onNumberOfEmployeesUpdated,
                onSaveClicked = onSaveClicked,
                )
        },
    )
}
