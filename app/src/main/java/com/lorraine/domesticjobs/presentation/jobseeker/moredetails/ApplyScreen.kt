package com.lorraine.domesticjobs.presentation.jobseeker.moredetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.domesticjobs.data.model.JobApplicationDetails
import com.lorraine.domesticjobs.presentation.components.TextFieldWithoutBorders

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
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.Start,
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
            val skillLevels = listOf("Beginner", "Intermediate", "Expert")
            val skillLevel by remember { mutableStateOf("") }
            var selectedSkillLevel by remember { mutableStateOf(skillLevel) }
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Skill level") //beginner, expert
                skillLevels.forEach { skillLevel ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = skillLevel == selectedSkillLevel,
                            onClick = {
                                selectedSkillLevel = skillLevel
                            },
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(skillLevel, fontSize = 15.sp)
                    }
                }
            }
            val yoeList = listOf("Less than 1", "1-2", "3-5", "over 5")
            val yoe by remember { mutableStateOf("") }
            var selectedYoe by remember { mutableStateOf(yoe) }
            Column(horizontalAlignment = Alignment.Start) {

                Text(text = "Years of experience") //less than year.
                yoeList.forEach { yoe ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = yoe == selectedYoe,
                            onClick = {
                                selectedYoe = yoe
                            },
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(yoe, fontSize = 15.sp)
                    }
                }
            }
            var additionalDetails by rememberSaveable {
                mutableStateOf("")
            }
            TextFieldWithoutBorders(
                value = additionalDetails,
                text = "Describe more about your self..",
                onValueChange = {
                    additionalDetails = it
                    viewModel.setApplicantExperienceDescription(
                        additionalDetails,
                        selectedYoe,
                        selectedSkillLevel
                    )
                })
            Button(modifier = Modifier.fillMaxWidth(0.7f), onClick = onApply) {
                Text(text = "Apply")
            }
        }
    }
}