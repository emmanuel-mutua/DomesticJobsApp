package com.johnstanley.attachmentapp.presentation.student.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.johnstanley.attachmentapp.presentation.student.components.HomeAppBar
import com.johnstanley.attachmentapp.presentation.student.components.NavigationDrawer

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeContent(
    drawerState: DrawerState,
    onMenuClicked: () -> Unit,
    onCalenderClicked: () -> Unit,
) {
    NavigationDrawer(drawerState = drawerState, onAddAttachmentDetails = {}) {
        Scaffold(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding(),
            topBar = {
                HomeAppBar(
                    onMenuClicked = onMenuClicked,
                    onCalenderClicked = onCalenderClicked,
                )
            },
            content = {
                TestScreen(text = "Home")
            },
        )
    }
}
