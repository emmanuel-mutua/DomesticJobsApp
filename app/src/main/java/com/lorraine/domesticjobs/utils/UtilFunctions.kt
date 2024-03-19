package com.lorraine.domesticjobs.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toInstant(): Instant {
    val zoneId = ZoneId.systemDefault() // Use the desired time zone

    return this.atZone(zoneId).toInstant()
}
