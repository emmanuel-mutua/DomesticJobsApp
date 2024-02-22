package com.lorraine.hiremequick.presentation.jobseeker.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.lorraine.hiremequick.data.model.ApplicationStatus
import com.lorraine.hiremequick.data.model.JobApplicationDetails
import com.lorraine.hiremequick.ui.theme.applicationStatus
import com.lorraine.hiremequick.ui.theme.bodyBold
import com.lorraine.hiremequick.ui.theme.bodyDescription

@Composable
fun JobApplicationHolder(
    jobApplication: JobApplicationDetails,
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
                Text(text = "Experience Description", style = bodyBold)
                Text(
                    modifier = Modifier.padding(all = 10.dp),
                    text = jobApplication.experienceDescription,
                    style = bodyDescription,
                )
                Text(text = "Application Status:", style = applicationStatus)
                var color = Color.Green
                 ApplicationStatus.entries.map {
                    color = when(it){
                        ApplicationStatus.PENDING -> Color.Green
                        ApplicationStatus.ACCEPTED -> Color.Red
                        ApplicationStatus.DECLINED -> Color.Red
                    }
                }
                Text(
                    modifier = Modifier.padding(all = 10.dp),
                    text = jobApplication.applicationStatus.name ?: "PENDING",
                    style = TextStyle(
                        color = color
                    ),
                )
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




