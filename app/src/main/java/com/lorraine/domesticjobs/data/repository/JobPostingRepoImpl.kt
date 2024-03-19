package com.lorraine.domesticjobs.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.data.model.RequestState
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
                val timestampMillis = it.datePosted
                val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
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
                val timestampMillis = it.datePosted
                val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
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
                val timestampMillis = it.datePosted
                val timestampInstant = Instant.ofEpochMilli(timestampMillis)
                timestampInstant.atZone(ZoneId.systemDefault()).toLocalDate()
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
            val jobPosting =
                document.documents[0].toObject(JobPosting::class.java)
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
        return try {
            jobPostingCollection.document(jobPosting.jobId).set(jobPosting).await()
            val insertedJob = jobPosting
            RequestState.Success(insertedJob)
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

    override suspend fun addApplicantIdToJobPosting(
        applicantId: String,
        jobId: String
    ): RequestState<Boolean> {

        return try {
            jobPostingCollection.document(jobId)
                .update("applicantIds", FieldValue.arrayUnion(applicantId))
                .addOnSuccessListener {
                    RequestState.Success(true)
                }
            RequestState.Success(true)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }
}
