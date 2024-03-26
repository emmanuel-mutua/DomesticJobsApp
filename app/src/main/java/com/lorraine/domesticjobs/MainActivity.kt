package com.lorraine.domesticjobs

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.lorraine.domesticjobs.presentation.auth.AuthNavGraph
import com.lorraine.domesticjobs.presentation.auth.AuthScreen
import com.lorraine.domesticjobs.presentation.auth.AuthViewModel
import com.lorraine.domesticjobs.ui.theme.AttachmentAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            AttachmentAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AttachmentApp()
                }
            }
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }
}

@Composable
fun AttachmentApp() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
    ) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = hiltViewModel()
        val startDestination =
            if (authViewModel.currentUser != null) {
                AuthScreen.Home.route
            } else {
                AuthScreen.Welcome.route
            }
        val registerState = authViewModel.authState.collectAsState().value
        AuthNavGraph(
            startDestination = startDestination,
            navController = navController,
            authViewModel = authViewModel,
            registerState = registerState,
        )
    }
}





