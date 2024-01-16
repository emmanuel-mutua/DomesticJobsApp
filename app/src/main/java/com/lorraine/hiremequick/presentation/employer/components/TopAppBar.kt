package com.lorraine.hiremequick.presentation.employer.components

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClicked: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Hamburger Menu Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = {
            Text(text = "Your Job Posting")
        },
        actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Date Icon",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
        },
    )
}
