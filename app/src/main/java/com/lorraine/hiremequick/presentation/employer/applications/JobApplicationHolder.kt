package com.lorraine.hiremequick.presentation.employer.applications

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lorraine.hiremequick.data.model.JobApplicationDetails
import com.lorraine.hiremequick.presentation.components.ContactHolder

@Composable
fun JobApplicationHolder(
    jobApplication: JobApplicationDetails,
    sendMessage: (String) -> Unit,
    sendEmail: (String) -> Unit,
    call: (String) -> Unit,
) {
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier.padding(top = 5.dp )
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
                Text(text = "Experience Description", style =TextStyle(fontSize = MaterialTheme.typography.titleLarge.fontSize))
                Text(
                    modifier = Modifier.padding(all = 14.dp),
                    text = jobApplication.experienceDescription,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                )
                Text(text = "Contact ${jobApplication.applicantName}", style =TextStyle(fontSize = MaterialTheme.typography.titleLarge.fontSize))
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
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
        )
    }
}




