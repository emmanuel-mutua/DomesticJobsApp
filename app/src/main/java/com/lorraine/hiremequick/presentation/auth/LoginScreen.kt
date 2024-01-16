package com.lorraine.hiremequick.presentation.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lorraine.hiremequick.R
import com.lorraine.hiremequick.presentation.components.MyOutlinedTextField
import com.lorraine.hiremequick.presentation.components.PassWordField
import com.lorraine.hiremequick.ui.theme.AttachmentAppTheme
import com.lorraine.hiremequick.utils.Contants

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    registerState: AuthStateData,
    viewModel: AuthViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val passwordVisible by rememberSaveable { mutableStateOf(false) }
    val isLoading = registerState.isLoading
    var isSignedIn by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val roles = listOf(Contants.Employer, Contants.JobSeeker)
    var role by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(role) }
    val signInResponse by viewModel.signInResponse.collectAsState()
    val isEmailVerified = viewModel.isEmailVerified
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        ,
        topBar = {
            TopAppBar(title = { Text(text = "Login") },
                navigationIcon = {
                    IconButton(
                        onClick =
                        navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                })
        }
    ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(state = rememberScrollState(), reverseScrolling = true),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.login),
                        contentDescription = "Login Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small)
                    )
                }

                MyOutlinedTextField(
                    value = email,
                    placeHolder = "Email",
                    onValueChange = { email = it },
                    isError = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))

                PassWordField(
                    isPasswordVisible = passwordVisible,
                    passwordValue = password,
                    label = "Password",
                    isError = false,
                    onValueChange = { password = it },
                )

                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    onClick = {
                        if (email == "" || password == "") {
                            Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        viewModel.signInEmailAndPassword(email, password) {
                            if (registerState.isSignedIn) {
                                Toast.makeText(context, registerState.message, Toast.LENGTH_SHORT)
                                    .show()
                                navigateToHome()
                            } else {
                                Toast.makeText(context, registerState.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(16),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    if (!isLoading) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            text = "Login",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                        )
                    } else {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun LoginScreenPrev() {
    AttachmentAppTheme {

    }
}
