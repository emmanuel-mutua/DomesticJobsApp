package com.lorraine.hiremequick.presentation.jobseeker.moredetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.hiremequick.data.model.JobPosting
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MoreDetailsScreen() {
    val viewModel : MoreDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val jobPosting = uiState.selectedJobId
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }


    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
            ) { },
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DiaryHeader(
                        title = jobPosting.title,
                        time = Instant.ofEpochMilli(jobPosting.datePosted)
                    )
                }
                Text(
                    modifier = Modifier.padding(all = 14.dp),
                    text = jobPosting.description,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Location"
                        )
                        Text(
                            text = " ${jobPosting.nameOfCountry} , ${jobPosting.nameOfCity}",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }

                    Surface(

                    ) {
                        MyClearBox(
                            text = jobPosting.modeOfWork,
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyClearBox(
                        text = "${jobPosting.applicantIds.size} Applicants",
                    )
                    Text(
                        modifier = Modifier.padding(all = 14.dp),
                        text = "${jobPosting.numberOfEmployeesNeeded} Employees Needed",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(all = 14.dp),
                        text = "Application DeadLine",
                        style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize),
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val formatter = remember {
                            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.getDefault())
                                .withZone(ZoneId.systemDefault())
                        }
                        Text(
                            text = "Date : ${formatter.format(Instant.ofEpochMilli(jobPosting.applicationDeadline))}",
                            color = MaterialTheme.colorScheme.error,
                            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryHeader(title: String, time: Instant) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
    }

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
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
            )
        }
        Text(
            text = "Posted ${formatter.format(time)}",
            color = MaterialTheme.colorScheme.onPrimary,
            style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(date: Instant) {

}

@Composable
fun MyClearBox(text: String) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = MaterialTheme.typography.titleMedium.fontSize),
        )
    }
}

