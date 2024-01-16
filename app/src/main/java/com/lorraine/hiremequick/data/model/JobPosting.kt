package com.lorraine.hiremequick.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.Instant
import java.util.UUID
val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
private val currentUserId = currentUser?.uid ?: ""

@RequiresApi(Build.VERSION_CODES.O)
data class JobPosting(
    var jobId: String = UUID.randomUUID().toString(),
    val employerId: String = currentUserId,
    var title: String = "",
    var description: String = "",
    var modeOfJob : String = "FullTime",
    var numberOfEmployeesNeeded : String = "",
    var nameOfCountry : String = "",
    var nameOfCity : String = "",
    var datePosted : Long = Instant.now().toEpochMilli(),
    var applicationDeadline : Long = Instant.now().toEpochMilli(),
    var applicantId : String = ""
)

