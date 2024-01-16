package com.lorraine.hiremequick.presentation.employer.add

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.presentation.employer.components.DatePickerIcon
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddJobPostingContent(
    uiState: UiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onModeOfWorkChanged: (String) -> Unit,
    onNumberOfEmployeesUpdated: (String) -> Unit,
    applicationDeadlineUpdated: (ZonedDateTime) -> Unit,
    paddingValues: PaddingValues,
    onSaveClicked: (JobPosting) -> Unit,
    onNameOfCountryChanged: (String) -> Unit,
    onNameOfCityChanged: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }
    Log.d("TAG", "AddJobContent: ${uiState.title}")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = 24.dp)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Job Details", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            JobPostingTextField(
                value = uiState.title,
                text = "Write Job title",
                onValueChange = {onTitleChanged.invoke(it)})
            JobPostingTextField(
                value = uiState.description,
                text = "Write description here (Skills, Experience ...)",
                onValueChange = {onDescriptionChanged.invoke(it)})
            JobPostingTextField(
                value = uiState.modeOfWork,
                text = "Specify (FullTime/PartTime)",
                onValueChange = {onModeOfWorkChanged.invoke(it)})
            JobPostingTextField(
                value = uiState.noOfEmployees,
                text = "Number of employees needed (1,2 ..)",
                onValueChange = {onNumberOfEmployeesUpdated.invoke(it)})
            Text(text = "Job Location", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            JobPostingTextField(
                value = uiState.nameOfCountry,
                text = "Enter the country",
                onValueChange = {onNameOfCountryChanged.invoke(it)})
            JobPostingTextField(
                value = uiState.nameOfCity,
                text = "Enter city name",
                onValueChange = {onNameOfCityChanged.invoke(it)})
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Select Application Deadline")
                DatePickerIcon(onDateTimeUpdated = applicationDeadlineUpdated)
            }


        }

        Column(verticalArrangement = Arrangement.Bottom) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
                    .height(54.dp),
                onClick = {
                    if (uiState.title.isNotEmpty() && uiState.description.isNotEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            onSaveClicked(
                                JobPosting().apply {
                                    this.title = uiState.title
                                    this.description = uiState.description
                                },
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Fields cannot be empty.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
                shape = Shapes().small,
            ) {
                Text(text = "Post Job")
            }
        }
    }
}

@Composable
fun JobPostingTextField(
    value : String,
    text : String,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = {onValueChange.invoke(it)},
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
