package com.johnstanley.attachmentapp.data.repository

import com.johnstanley.attachmentapp.data.model.AttachmentLog
import com.johnstanley.attachmentapp.data.model.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface AttachmentLogRepo {
    suspend fun getAllAttachmentLogs(studentId : String): Flow<AttachmentLogs>
    suspend fun getFilteredAttachmentLogs(zonedDateTime: ZonedDateTime, studentId: String): Flow<AttachmentLogs>
    suspend fun getSelectedAttachmentLog(attachmentId: String): RequestState<AttachmentLog>
    suspend fun insertAttachmentLog(attachmentLog: AttachmentLog): RequestState<AttachmentLog>
    suspend fun updateAttachmentLog(attachmentLog: AttachmentLog): RequestState<AttachmentLog>
    suspend fun deleteAttachmentLog(id: String): RequestState<Boolean>
    suspend fun deleteAttachmentLog(): RequestState<Boolean>
}
