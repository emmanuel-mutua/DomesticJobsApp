package com.lorraine.hiremequick.presentation.jobseeker.moredetails

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MoreDetailsScreen(
    jobPosting: JobPosting,
    navigateBack: () -> Unit
) {
    val viewModel: MoreDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val localDensity = LocalDensity.current
    var componentHeight by remember { mutableStateOf(0.dp) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                title = {
                    Text(text = jobPosting.title)
                })
        }
    ) {

        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        modifier = Modifier.padding(all = 10.dp),
                        text = jobPosting.description,
                        style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
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

                        MyClearBox(
                            text = jobPosting.modeOfWork,
                        )
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
                                DateTimeFormatter.ofPattern(
                                    "d MMM uuuu",
                                    Locale.getDefault()
                                )
                                    .withZone(ZoneId.systemDefault())
                            }
                            Text(
                                text = "Date : ${
                                    formatter.format(
                                        Instant.ofEpochMilli(
                                            jobPosting.applicationDeadline
                                        )
                                    )
                                }",
                                color = MaterialTheme.colorScheme.error,
                                style = TextStyle(fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                            )
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Apply")
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

