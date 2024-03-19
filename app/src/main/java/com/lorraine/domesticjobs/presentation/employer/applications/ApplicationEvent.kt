package com.lorraine.domesticjobs.presentation.employer.applications

import android.content.Context

sealed class ApplicationEvent {
    data class SendAcceptanceEmailEvent(val context: Context) : ApplicationEvent()
    data class SendDeclineEmailEvent(val context: Context) : ApplicationEvent()
}