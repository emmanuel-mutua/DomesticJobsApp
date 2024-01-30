package com.lorraine.hiremequick.data.model

import java.time.Instant

data class JobPosting(
    var jobId: String = "",
    var employerId: String = "",
    var title: String = "",
    var description: String = "",
    var modeOfWork : String = "", //FullTime/PartTime
    var numberOfEmployeesNeeded : String = "",
    var nameOfCountry : String = "",
    var nameOfCity : String = "",
    var datePosted : Long = Instant.now().toEpochMilli(),
    var applicationDeadline : Long = 0L,
    var applicantIds : List<String> = emptyList()
)

