package com.lorraine.hiremequick.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.data.model.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object FirebaseJobPostingRepo : JobPostingRepo {

    private val firestore = FirebaseFirestore.getInstance()
    private val jobPostingCollection = firestore.collection("jobPosting")

    override suspend fun getAllJobsPostings(employerId: String): Flow<JobPostings> {
        return try {
            val snapshot = jobPostingCollection
                .whereEqualTo("employerId", employerId)
                .get().await()
            val logs = snapshot.toObjects(JobPosting::class.java).groupBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timestampMillis = it.datePosted
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

    override suspend fun getAllJobsPostings(): Flow<JobPostings> {
        return try {
            val snapshot = jobPostingCollection
                .get().await()
            val jobs = snapshot.toObjects(JobPosting::class.java).groupBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timestampMillis = it.datePosted
                    val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                    timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }
            flow {
                emit(
                    RequestState.Success(
                        data = jobs,
                    ),
                )
            }
        } catch (e: Exception) {
            flow { emit(RequestState.Error(e)) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFilteredJobPostings(
        zonedDateTime: ZonedDateTime,
        studentId: String,
    ): Flow<JobPostings> =

        try {
            // Implement the query based on your requirements
            // You can use whereEqualTo, whereGreaterThan, etc.
            val snapshot = jobPostingCollection
                .whereEqualTo("studentId", studentId)
                .whereGreaterThanOrEqualTo(
                    "date",
                    zonedDateTime.toInstant().toEpochMilli(),
                )
                .get()
                .await()

            val logs = snapshot.toObjects(JobPosting::class.java).groupBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timestampMillis = it.datePosted
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

    override suspend fun getSelectedJob(jobId: String): RequestState<JobPosting> =
        try {
            val document = jobPostingCollection
                .whereEqualTo("jobId", jobId)
                .get().await()
            val jobPosting = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                document.documents[0].toObject(JobPosting::class.java)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            Log.d("TAG", "getSelectedJob: ${jobPosting?.title}")
            if (jobPosting != null) {
                RequestState.Success(jobPosting)
            } else {
                RequestState.Error(Exception("Job posting not found"))
            }
        } catch (e: Exception) {
            RequestState.Error(e)
        }

    override suspend fun insertJob(jobPosting: JobPosting): RequestState<JobPosting> {
        return try { jobPostingCollection.document(jobPosting.jobId).set(jobPosting).await()
            val insertedLog = jobPosting
            RequestState.Success(insertedLog)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun updateJobPosting(jobPosting: JobPosting): RequestState<JobPosting> {
        return try {
            jobPostingCollection.document(jobPosting.jobId)
                .set(jobPosting).await()
            RequestState.Success(jobPosting)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun deleteJobPosting(id: String): RequestState<Boolean> {
        return try {
            jobPostingCollection.document(id).delete().await()
            RequestState.Success(true)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun deleteJobPosting(): RequestState<Boolean> {
        TODO("Not yet implemented")
    }
}
