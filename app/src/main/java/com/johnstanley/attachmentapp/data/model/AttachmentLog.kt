package com.johnstanley.attachmentapp.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.Instant
import java.util.UUID
val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
private val currentUserId = currentUser?.uid ?: ""

@RequiresApi(Build.VERSION_CODES.O)
data class AttachmentLog(
    var id: String = UUID.randomUUID().toString(),
    val studentId: String = currentUserId,
    var title: String = "",
    var description: String = "",
    var images: List<String> = emptyList(),
    var date : Long = Instant.now().toEpochMilli(),
)

