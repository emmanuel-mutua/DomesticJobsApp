package com.lorraine.domesticjobs.utils

import android.content.Context
import android.widget.Toast
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toInstant(): Instant {
    val zoneId = ZoneId.systemDefault() // Use the desired time zone

    return this.atZone(zoneId).toInstant()
}

fun showToast(context: Context, message : String){
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG,
    ).show()
}
