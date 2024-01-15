package com.johnstanley.attachmentapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.johnstanley.attachmentapp.data.database.ImageToDeleteDao
import com.johnstanley.attachmentapp.data.database.ImageToUploadDao
import com.johnstanley.attachmentapp.presentation.auth.AuthNavGraph
import com.johnstanley.attachmentapp.presentation.auth.AuthScreen
import com.johnstanley.attachmentapp.presentation.auth.AuthViewModel
import com.johnstanley.attachmentapp.ui.theme.AttachmentAppTheme
import com.johnstanley.attachmentapp.utils.retryDeletingImageFromFirebase
import com.johnstanley.attachmentapp.utils.retryUploadingImageToFirebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao
    private var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
//        cleanupCheck(
//            scope = lifecycleScope,
//            imageToUploadDao = imageToUploadDao,
//            imageToDeleteDao = imageToDeleteDao,
//        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AttachmentApp(name: String, modifier: Modifier = Modifier, activity : Activity) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.statusBarsPadding(),
    ) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = hiltViewModel()
        val startDestination =
            if (authViewModel.currentUser != null && authViewModel.isEmailVerified) {
                AuthScreen.Home.route
            } else {
                AuthScreen.Login.route
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

private fun cleanupCheck(
    scope: CoroutineScope,
    imageToUploadDao: ImageToUploadDao,
    imageToDeleteDao: ImageToDeleteDao,
) {
    scope.launch(Dispatchers.IO) {
        val result = imageToUploadDao.getAllImages()
        result.forEach { imageToUpload ->
            retryUploadingImageToFirebase(
                imageToUpload = imageToUpload,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imageToUploadDao.cleanupImage(imageId = imageToUpload.id)
                    }
                },
            )
        }
        val result2 = imageToDeleteDao.getAllImages()
        result2.forEach { imageToDelete ->
            retryDeletingImageFromFirebase(
                imageToDelete = imageToDelete,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imageToDeleteDao.cleanupImage(imageId = imageToDelete.id)
                    }
                },
            )
        }
    }
}


