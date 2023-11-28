package com.johnstanley.attachmentapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstanley.attachmentapp.data.model.AttachmentLog
import com.johnstanley.attachmentapp.data.model.RequestState
import com.johnstanley.attachmentapp.utils.toInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object FirebaseAttachmentLogRepo : AttachmentLogRepo {

    private val firestore = FirebaseFirestore.getInstance()
    private val attachmentLogsCollection = firestore.collection("attachmentLogs")

    override suspend fun getAllAttachmentLogs(): Flow<AttachmentLogs> {
        return try {
            val snapshot = attachmentLogsCollection.get().await()
            val logs = snapshot.toObjects(AttachmentLog::class.java).groupBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timestampMillis = it.date
                    val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                    timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }
            flow {
                emit(
                    RequestState.Success(
                        data = logs,
                    ),
                )
            }
        } catch (e: Exception) {
            flow { emit(RequestState.Error(e)) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFilteredAttachmentLogs(zonedDateTime: ZonedDateTime): Flow<AttachmentLogs> =

        try {
            // Implement the query based on your requirements
            // You can use whereEqualTo, whereGreaterThan, etc.
            val snapshot = attachmentLogsCollection
                .whereGreaterThan(
                    "date",
                    Timestamp(zonedDateTime.toInstant().toEpochMilli(), 0).toDate(),
                )
                .get()
                .await()

            val logs = snapshot.toObjects(AttachmentLog::class.java).groupBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timestampMillis = it.date
                    val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                    timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }
            flow {
                emit(
                    RequestState.Success(
                        data = logs,
                    ),
                )
            }
        } catch (e: Exception) {
            flow { emit(RequestState.Error(e)) }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getSelectedAttachmentLog(attachmentId: String): Flow<RequestState<AttachmentLog>> =
        flow {
            try {
                val document = attachmentLogsCollection.document(attachmentId).get().await()
                val attachmentLog = document.toObject(AttachmentLog::class.java)
                if (attachmentLog != null) {
                    emit(RequestState.Success(attachmentLog))
                } else {
                    emit(RequestState.Error(Exception("AttachmentLog not found")))
                }
            } catch (e: Exception) {
                emit(RequestState.Error(e))
            }
        }

    override suspend fun insertAttachmentLog(attachmentLog: AttachmentLog): RequestState<AttachmentLog> {
        return try {
            val document = attachmentLogsCollection.add(attachmentLog).await()
            val insertedLog = attachmentLog.copy(id = document.id)
            RequestState.Success(insertedLog)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun updateAttachmentLog(attachmentLog: AttachmentLog): RequestState<AttachmentLog> {
        return try {
            attachmentLogsCollection.document(attachmentLog.id).set(attachmentLog).await()
            RequestState.Success(attachmentLog)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun deleteAttachmentLog(id: String): RequestState<Boolean> {
        return try {
            attachmentLogsCollection.document(id).delete().await()
            RequestState.Success(true)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun deleteAttachmentLog(): RequestState<Boolean> {
        TODO("Not yet implemented")
    }
}
