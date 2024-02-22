package com.lorraine.hiremequick.data.model


data class JobApplicationDetails(
    val applicantName: String = "",
    val applicantEmail: String = "",
    val applicantPhoneNumber: String = "",
    val experienceDescription: String = "",
    val jobTitle: String = "",
    val employerId: String = "",
    val selectedJobId: String = "",
    val applicantId: String = "",
    val applicationStatus : ApplicationStatus = ApplicationStatus.PENDING
)