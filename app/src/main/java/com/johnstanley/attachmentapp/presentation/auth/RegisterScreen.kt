package com.johnstanley.attachmentapp.presentation.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.johnstanley.attachmentapp.presentation.components.MyOutlinedTextField
import com.johnstanley.attachmentapp.presentation.components.PassWordField
import com.johnstanley.attachmentapp.ui.theme.AttachmentAppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    userData: UserData,
) {
    val passwordVisible by rememberSaveable { mutableStateOf(false) }
    val isLoading = userData.isLoading
    val fullName = userData.fullName
    val registrationNumber = userData.registrationNumber
    val email = userData.email
    val phoneNumber = userData.phoneNumber
    val role = userData.role
    val password = userData.password
    val confirmPassword = userData.confirmPassword
    var selectedRole by remember { mutableStateOf(role) }
    val roles = listOf("Staff", "Student")

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Welcome!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Register an account",
                fontSize = 19.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp, start = 50.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Role:")
                roles.forEach { roleOption ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = roleOption == selectedRole,
                            onClick = {
                                selectedRole = roleOption
                                viewModel.setRole(roleOption)
                            },
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(roleOption)
                    }
                }
            }

            MyOutlinedTextField(
                value = fullName,
                placeHolder = "FullName",
                onValueChange = { viewModel.setFullName(it) },
                isError = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                ),
            )
            if (selectedRole == "Student") {
                MyOutlinedTextField(
                    value = registrationNumber,
                    placeHolder = "Registration Number",
                    onValueChange = { viewModel.setRegNoName(it) },
                    isError = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                )
            }

            MyOutlinedTextField(
                value = phoneNumber,
                placeHolder = "Phone Number",
                onValueChange = { viewModel.setPhoneNumber(it) },
                isError = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                ),
            )

            MyOutlinedTextField(
                value = email,
                placeHolder = "Email",
                onValueChange = { viewModel.setEmail(it) },
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
                onValueChange = { viewModel.setPassword(it) },
            )
            PassWordField(
                isPasswordVisible = passwordVisible,
                passwordValue = confirmPassword,
                label = "ConfirmPassword",
                onValueChange = { viewModel.setConfirmPassword(it) },
            )

//            if (passwordState.error != "") {
//                Text(
//                    text = passwordState.error ?: "",
//                    style = MaterialTheme.typography.body2,
//                    color = MaterialTheme.colors.error,
//                    textAlign = TextAlgn.End,
//                  modifier = Modifier.fillMaxWidth(),
//                )
//            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.registerUser()
                },
                shape = RoundedCornerShape(16),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    text = "Register",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                )
            }
            TextButton(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(),
                        ) {
                            append("Already have an account?")
                        }
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append("Login")
                        }
                    },
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPrev() {
    AttachmentAppTheme {
    }
}
