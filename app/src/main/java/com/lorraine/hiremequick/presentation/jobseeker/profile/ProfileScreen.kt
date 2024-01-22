package com.lorraine.hiremequick.presentation.jobseeker.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lorraine.hiremequick.data.model.JobSeekerData


@Composable
fun ProfileScreen(
    onSignOutClicked: () -> Unit,
    uiState : JobSeekerData
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box (
            modifier = Modifier.clip(CircleShape)
        ){
            Image(modifier = Modifier.size(100.dp),imageVector = Icons.Filled.Person, contentDescription ="Profile" )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "${uiState.fullName}")
            Text(text = "${uiState.email}")
            Text(text = "${uiState.phoneNumber}")
        }
        TextButton(modifier = Modifier.padding(top = 30.dp),onClick = onSignOutClicked) {
            Text(text = "Sign Out")
        }
    }
}