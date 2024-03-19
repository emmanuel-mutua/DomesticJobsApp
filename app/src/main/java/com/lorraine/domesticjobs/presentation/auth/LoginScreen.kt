package com.lorraine.domesticjobs.presentation.auth

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.lorraine.domesticjobs.R
import com.lorraine.domesticjobs.presentation.components.MyOutlinedTextField
import com.lorraine.domesticjobs.presentation.components.PassWordField
import com.lorraine.domesticjobs.ui.theme.AttachmentAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    registerState: AuthStateData,
    viewModel: AuthViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToForgotPassword: () -> Unit,
) {
    val passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.signInEventResponse.collect { event ->
            when (event) {
                is SignInEventResponse.Loading -> {
                    isLoading = true
                }

                is SignInEventResponse.Message -> {
                    isLoading = false
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is SignInEventResponse.Success -> {
                    isLoading = false
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
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
            TextButton(onClick = navigateToForgotPassword) {
                Text(text = "Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (email == "" || password == "") {
                        Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    viewModel.signInEmailAndPassword(email, password)
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

private fun showToast(context : Context, message : String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPrev() {
    AttachmentAppTheme {
        val vm : AuthViewModel = hiltViewModel()
        LoginScreen(
            registerState = AuthStateData(),
            viewModel = vm,
            {}, {}, {}
        )
    }
}
