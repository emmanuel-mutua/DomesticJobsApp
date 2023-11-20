package com.johnstanley.attachmentapp.presentation.components

import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    dialogOpened: Boolean,
    onCloseDialog: () -> Unit,
    onYesClicked: () -> Unit,
) {
    if (dialogOpened) {
        AlertDialog(
            title = {
                Text(
                    modifier = Modifier,
                    text = title,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Light,
                )
            },
            onDismissRequest = onCloseDialog,
            confirmButton = {
                Button(onClick = onYesClicked) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onCloseDialog) {
                    Text(text = "No")
                }
            },
        )
    }
}
