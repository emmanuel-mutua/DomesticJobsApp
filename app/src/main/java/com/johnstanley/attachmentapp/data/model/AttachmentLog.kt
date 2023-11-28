package com.johnstanley.attachmentapp.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
data class AttachmentLog(
    var id: String = UUID.randomUUID().toString(),
    val studentId: String = "",
    var title: String = "",
    var description: String = "",
    var images: List<String> = emptyList(),
    var date : Long = Instant.now().toEpochMilli(),
)

