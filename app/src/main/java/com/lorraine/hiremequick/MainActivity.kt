package com.lorraine.hiremequick

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.lorraine.hiremequick.presentation.auth.AuthNavGraph
import com.lorraine.hiremequick.presentation.auth.AuthScreen
import com.lorraine.hiremequick.presentation.auth.AuthViewModel
import com.lorraine.hiremequick.ui.theme.AttachmentAppTheme

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            AttachmentAppTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AttachmentApp(name = "AttachmentApp", activity = this)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AttachmentApp(name: String, modifier: Modifier = Modifier, activity : Activity) {
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
        val registerState = authViewModel.registerState.collectAsState().value
        AuthNavGraph(
            startDestination = startDestination,
            navController = navController,
            authViewModel = authViewModel,
            registerState = registerState,
            activity = activity
        )
    }
}





