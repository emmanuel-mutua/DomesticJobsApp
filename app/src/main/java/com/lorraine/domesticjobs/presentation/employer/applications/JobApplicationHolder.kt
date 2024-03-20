package com.lorraine.domesticjobs.presentation.employer.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lorraine.domesticjobs.data.model.ApplicationStatus
import com.lorraine.domesticjobs.data.model.JobApplicationDetails
import com.lorraine.domesticjobs.presentation.components.ContactHolder
import com.lorraine.domesticjobs.ui.theme.bodyBold
import com.lorraine.domesticjobs.ui.theme.bodyDescription

@Composable
fun JobApplicationHolder(
    jobApplication: JobApplicationDetails,
    sendMessage: (String) -> Unit,
    sendEmail: (String) -> Unit,
    call: (String) -> Unit,
    acceptJobSeeker: (String, String) -> Unit,
    declineJobSeeker: (String, String) -> Unit,
) {
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier.padding(top = 5.dp)
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(componentHeight + 14.dp),
            tonalElevation = 0.dp,
        ) {}
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = 0.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                JobApplicationHolderHeader(
                    title = jobApplication.jobTitle,
                )
                Text(text = "Experience Description", style = bodyBold)
                Text(
                    modifier = Modifier.padding(all = 14.dp),
                    text = jobApplication.experienceDescription,
                    style = bodyDescription,
                )
                Text(text = "Contact ${jobApplication.applicantName}", style = bodyBold)

                Row {
                    Text(text = "Email:  ${jobApplication.applicantEmail} ", style = bodyBold)
                    Text(text = " Tel ${jobApplication.applicantPhoneNumber}", style = bodyBold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ContactHolder(imageVector = Icons.Default.Email, text = "Send Email") {
                        sendEmail.invoke(jobApplication.applicantEmail)
                    }
                    ContactHolder(imageVector = Icons.Default.Call, text = "Call") {
                        call.invoke(jobApplication.applicantPhoneNumber)
                    }
                    ContactHolder(imageVector = Icons.Default.MailOutline, text = "Send Message") {
                        sendMessage.invoke(jobApplication.applicantPhoneNumber)
                    }
                }
                val acceptanceText = when (jobApplication.applicationStatus) {
                    ApplicationStatus.PENDING -> "Accept"
                    ApplicationStatus.ACCEPTED -> "Already Accepted"
                    ApplicationStatus.DECLINED -> "Accept"
                }
                val declineText = when (jobApplication.applicationStatus) {
                    ApplicationStatus.PENDING -> "Decline"
                    ApplicationStatus.ACCEPTED -> "Decline"
                    ApplicationStatus.DECLINED -> "Already Declined"
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = {
                        acceptJobSeeker.invoke(
                            jobApplication.applicantId,
                            jobApplication.selectedJobId
                        )
                    }) {
                        Text(text = acceptanceText)
                    }
                    OutlinedButton(onClick = {
                        declineJobSeeker.invoke(
                            jobApplication.applicantId,
                            jobApplication.selectedJobId
                        )
                    }) {
                        Text(text = declineText)
                    }
                }

            }
        }
    }
}

@Composable
fun JobApplicationHolderHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "JobTitle:",
                color = MaterialTheme.colorScheme.onPrimary,
                style = bodyBold,
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = bodyBold,
        )
    }
}




