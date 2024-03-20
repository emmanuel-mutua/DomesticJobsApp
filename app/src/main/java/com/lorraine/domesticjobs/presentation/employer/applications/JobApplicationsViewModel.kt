package com.lorraine.domesticjobs.presentation.employer.applications

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lorraine.domesticjobs.data.model.ApplicationStatus
import com.lorraine.domesticjobs.data.model.JobApplicationDetails
import com.lorraine.domesticjobs.data.model.RequestState
import com.lorraine.domesticjobs.data.repository.JobApplicationRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

enum class DownloadStatus {
    STARTING, FAILED, FINISHED
}

@HiltViewModel
class JobApplicationsViewModel @Inject constructor(

) : ViewModel() {
    private var _uiState = MutableStateFlow(JobApplicationsUiState())
    val uiState = _uiState.asStateFlow()

    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val employerId = currentUser?.uid!!

    private val _channel = Channel<DownloadStatus>()
    val channel = _channel.receiveAsFlow()

    init {

    }

    fun getApplications(jobId: String?) {
        getJobApplications(employerId, jobId)
    }

    fun getJobApplications(employerId: String, jobId: String?) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            Log.d("JobApplicationsViewModel", ": Getting data for job id: $jobId")
            val applications = JobApplicationRepoImpl.getJobApplications(employerId, jobId)
            when (applications) {
                is RequestState.Success -> {
                    applications.data.collectLatest { jobApplications ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                jobApplications = jobApplications
                            )
                        }
                    }
                }

                is RequestState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error Fetching applications"
                        )
                    }
                }

                RequestState.Loading -> {}
                RequestState.Idle -> {}
            }
        }
    }

    fun sendMessage(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        context.startActivity(Intent.createChooser(intent, "Choose a messaging app"))
    }

    fun callApplicant(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(Intent.createChooser(intent, "Choose a calling app"))
    }

    fun sendEmail(context: Context, emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$emailAddress")
        context.startActivity(Intent.createChooser(intent, "Send email"))
    }

    fun acceptJobSeeker(applicantId: String, jobId: String) {
        viewModelScope.launch {
            JobApplicationRepoImpl.acceptJobSeeker(applicantId, jobId)
        }
    }

    fun declineJobSeeker(applicantId: String, jobId: String) {
        viewModelScope.launch {
            JobApplicationRepoImpl.declineJobSeeker(applicantId, jobId)
        }
    }

    fun onApplicationEvent(event: ApplicationEvent) {
        when (event) {
            is ApplicationEvent.SendAcceptanceEmailEvent -> {
                sendAcceptanceEmails(event.context)
            }

            is ApplicationEvent.SendDeclineEmailEvent -> {
                sendDeclineEmails(event.context)
            }
        }
    }

    private fun sendAcceptanceEmails(context: Context) {
        Log.d("SendEmail", "sendAcceptanceEmails: initialized")
        val listOfEmails: ArrayList<String> = getAcceptedEmailAddresses() ?: return
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$listOfEmails")
        intent.putExtra(Intent.EXTRA_EMAIL, listOfEmails)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Acceptance")
        intent.putExtra(Intent.EXTRA_TEXT, "Write acceptance email body")
        context.startActivity(intent)
    }

    private fun sendDeclineEmails(context: Context) {
        val listOfEmails: ArrayList<String> = getDeclinedEmailAddresses() ?: return
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, listOfEmails)
            putExtra(Intent.EXTRA_SUBJECT, "Decline Draft")
            putExtra(Intent.EXTRA_TEXT, "Write decline email body")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            TODO("handle email failure")
        }
    }

    private fun getAcceptedEmailAddresses(): ArrayList<String>? {
        val myArray: ArrayList<String> = arrayListOf()
        _uiState.update {
            it.copy(
                isLoadingAcceptanceEmails = true
            )
        }
        _uiState.value.jobApplications.forEach { application ->
            if (application.applicationStatus == ApplicationStatus.ACCEPTED) {
                myArray.add(application.applicantEmail)
            }
        }
        _uiState.update {
            it.copy(
                isLoadingAcceptanceEmails = false
            )
        }
        return myArray
    }

    private fun getDeclinedEmailAddresses(): ArrayList<String>? {
        val myArray: ArrayList<String> = arrayListOf()
        _uiState.update {
            it.copy(
                isLoadingDeclineEmails = true
            )
        }
        _uiState.value.jobApplications.forEach { application ->
            if (application.applicationStatus == ApplicationStatus.DECLINED || application.applicationStatus == ApplicationStatus.PENDING) {
                myArray.add(application.applicantEmail)
            }
        }
        _uiState.update {
            it.copy(
                isLoadingDeclineEmails = false
            )
        }
        return myArray
    }

    fun downloadApplications(applications: List<JobApplicationDetails>, context: Context) {
        viewModelScope.launch {
            _channel.send(DownloadStatus.STARTING)
            // call the repo to download the excel file with this applications, and the columns of each row are provided below
            //new excel sheet
            val workBook = XSSFWorkbook()
            val sheet = workBook.createSheet("Job Applications")

            // Create header row
            val headerRow = sheet.createRow(0)
            val headers = arrayOf(
                "Applicant Name",
                "Applicant Email",
                "Applicant Phone Number",
                "Experience Description",
                "Job Title",
                "Employer ID",
                "Selected Job ID",
                "Applicant ID",
                "Application Status"
            )
            for ((index, header) in headers.withIndex()) {
                headerRow.createCell(index).setCellValue(header)
            }
            // Populate data rows
            var rowNum = 1
            for (application in applications) {
                val row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(application.applicantName)
                row.createCell(1).setCellValue(application.applicantEmail)
                row.createCell(2).setCellValue(application.applicantPhoneNumber)
                row.createCell(3).setCellValue(application.experienceDescription)
                row.createCell(4).setCellValue(application.jobTitle)
                row.createCell(5).setCellValue(application.employerId)
                row.createCell(6).setCellValue(application.selectedJobId)
                row.createCell(7).setCellValue(application.applicantId)
                row.createCell(8).setCellValue(application.applicationStatus.toString())
            }
            // Get the directory for the app's private files
            val fileDir = context.filesDir
            // Create the file path
            val filePath = File(fileDir, "job_applications.xlsx")
            val fileOutputStream: FileOutputStream = FileOutputStream(filePath)
            workBook.write(fileOutputStream)
            // Add the file to MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "job_applications.xlsx")
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                // For API level 29 and above, you may also need to put MediaStore.MediaColumns.RELATIVE_PATH
            }
            val resolver: ContentResolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            // Write the file data to the newly created URI
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    filePath.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            _uiState.update {
                it.copy(
                    isDownloadingApplications = false
                )
            }
            _channel.send(DownloadStatus.FINISHED)
        }
    }


}

data class JobApplicationsUiState(
    val jobApplications: List<JobApplicationDetails> = emptyList(),
    val emailDeclinedAddresses: ArrayList<String> = arrayListOf(),
    val emailAcceptedAddresses: ArrayList<String> = arrayListOf(),
    val isLoading: Boolean = false,
    val isDownloadingApplications: Boolean = false,
    val isLoadingDeclineEmails: Boolean = false,
    val isLoadingAcceptanceEmails: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)