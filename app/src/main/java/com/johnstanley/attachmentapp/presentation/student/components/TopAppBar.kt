package com.johnstanley.attachmentapp.presentation.student.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    onMenuClicked: () -> Unit,
    onCalenderClicked: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Home navigation Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = {
            Text(text = "AttachLog Pro")
        },
        actions = {
            IconButton(onClick = onCalenderClicked) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calender Icon")
            }
        },
    )
}
