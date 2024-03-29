package com.lorraine.domesticjobs.presentation.employer.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.presentation.employer.components.JobPostingHolder
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployerHomeContent(
    paddingValues: PaddingValues,
    jobPosting: Map<LocalDate, List<JobPosting>>,
    onClick: (String) -> Unit,
    navigateToApplications: (String) -> Unit,
) {
    if (jobPosting.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 55.dp)
                .navigationBarsPadding()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            jobPosting.forEach { (localDate, jobs) ->
                stickyHeader(key = localDate) {
                        DateHeader(localDate = localDate)
                }
                items(
                    items = jobs,
                    key = { it.jobId },
                ) {
                    JobPostingHolder(
                        jobPosting = it,
                        onApplicantsClick = navigateToApplications,
                        onClick = onClick)
                }
            }
        }
    } else {
        EmptyPage()
    }
}

@Composable
fun DateHeader(localDate: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = String.format("%02d", localDate.dayOfMonth),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light,
                ),
            )
            Text(
                text = localDate.dayOfWeek.toString().take(3),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light,
                ),
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = localDate.month.toString().lowercase()
                    .replaceFirstChar { it.titlecase() },
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Light,
                ),
            )
            Text(
                text = "${localDate.year}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = FontWeight.Light,
                ),
            )
        }
    }
}

@Composable
fun EmptyPage(
    title: String = "No jobs posted",
    subtitle: String = "Add jobs",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium,
            ),
        )
        Text(
            text = subtitle,
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}
