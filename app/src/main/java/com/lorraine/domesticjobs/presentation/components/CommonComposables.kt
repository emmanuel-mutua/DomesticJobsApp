package com.lorraine.domesticjobs.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lorraine.domesticjobs.R
import com.lorraine.domesticjobs.ui.theme.bodyDescription

@Composable
fun MyOutlinedTextField(
    value: String,
    placeHolder: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    keyboardOptions: KeyboardOptions,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        singleLine = true,
        onValueChange = { onValueChange(it) },
        placeholder = {
            Text(text = placeHolder)
        },
        keyboardOptions = keyboardOptions,
        isError = isError,
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun PassWordField(
    isPasswordVisible: Boolean,
    passwordValue: String,
    label: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    var passwordVisible by rememberSaveable { mutableStateOf(isPasswordVisible) }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = passwordValue,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        label = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        trailingIcon = {
            val imageResource = if (passwordVisible) {
                R.drawable.baseline_visibility_24
            } else {
                R.drawable.baseline_visibility_off_24
            }
            // Localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            // Toggle button to hide or display password
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = painterResource(id = imageResource), contentDescription = "")
            }
        },
    )
    Spacer(modifier = Modifier.height(5.dp))
}


@Composable
fun TextFieldWithoutBorders(
    value: String,
    text: String,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange.invoke(it) },
        placeholder = { Text(text = text) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Unspecified,
            disabledIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.clearFocus()
            },
        ),
    )
}

@Composable
fun MyClearBox(text: String) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = bodyDescription,
        )
    }
}

@Composable
fun ContactHolder(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable {
                onClick.invoke()
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = imageVector, tint = MaterialTheme.colorScheme.secondary,contentDescription = "Contact box")
        Text(
            text = text,
            style = bodyDescription,
        )
    }
}

@Composable
fun MyProgressIndicator() {
    Column {
        CircularProgressIndicator()
    }
}
