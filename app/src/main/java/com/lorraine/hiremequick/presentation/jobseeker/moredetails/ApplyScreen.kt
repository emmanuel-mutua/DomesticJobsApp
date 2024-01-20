package com.lorraine.hiremequick.presentation.jobseeker.moredetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.hiremequick.presentation.components.TextFieldWithoutBorders

@Composable
fun ApplyJobScreen(
    onApply: () -> Unit,
    applicationDetails: JobApplicationDetails,
    viewModel: MoreDetailsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Please fill these details")
            TextFieldWithoutBorders(
                value = applicationDetails.applicantName,
                text = "FullName",
                onValueChange = { viewModel.setApplicantName(it) })
            TextFieldWithoutBorders(
                value = applicationDetails.applicantEmail,
                text = "Email",
                onValueChange = { viewModel.setApplicantEmail(it) })
            TextFieldWithoutBorders(
                value = applicationDetails.applicantPhoneNumber,
                text = "PhoneNumber",
                onValueChange = { viewModel.setApplicantPhoneNumber(it) })
            TextFieldWithoutBorders(
                value = applicationDetails.experienceDescription,
                text = "Describe your experience (Skills, years of experience, ...",
                onValueChange = { viewModel.setApplicantExperienceDescription(it) })
            Button(modifier = Modifier.fillMaxWidth(0.7f), onClick = onApply) {
                Text(text = "Apply")
            }
        }
    }
}