package com.lorraine.hiremequick.presentation.staff.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobSeekerHomeScreen() {
    val messageBarState = rememberMessageBarState()
    Scaffold(
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding().navigationBarsPadding(),
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                messageBarState.addSuccess("Welcome Back Staff")
            }
        },
    )
}
