package com.lorraine.domesticjobs.presentation.employer.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lorraine.domesticjobs.presentation.employer.add.UiState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerIcon(
    uiState: UiState,
    onDateTimeUpdated: (ZonedDateTime) -> Unit
) {
    val dateDialog = rememberUseCaseState()
    val timeDialog = rememberUseCaseState()
    var currentDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    var dateTimeUpdated by remember { mutableStateOf(false) }


    IconButton(onClick = {
        dateTimeUpdated = false
        onDateTimeUpdated(
            ZonedDateTime.of(
                currentDate,
                currentTime,
                ZoneId.systemDefault(),
            ),
        )
    }) {
        IconButton(onClick = { dateDialog.show() }) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date Icon",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    CalendarDialog(
        state = dateDialog,
        selection = CalendarSelection.Date { localDate ->
            currentDate = localDate
            timeDialog.show()
        },
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            disabledDates = uiState.disabledDates
        ),
    )
    ClockDialog(
        state = timeDialog,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            currentTime = LocalTime.of(hours, minutes)
            dateTimeUpdated = true
            onDateTimeUpdated(
                ZonedDateTime.of(
                    currentDate,
                    currentTime,
                    ZoneId.systemDefault(),
                ),
            )
        },
    )
}