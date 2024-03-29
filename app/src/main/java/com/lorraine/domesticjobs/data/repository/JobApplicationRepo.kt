package com.lorraine.domesticjobs.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.lorraine.domesticjobs.data.model.ApplicationStatus
import com.lorraine.domesticjobs.data.model.JobApplicationDetails
import com.lorraine.domesticjobs.data.model.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface JobApplicationRepo {
    suspend fun insertJobApplication(jobApplicationDetails: JobApplicationDetails): RequestState<JobApplicationDetails>
    suspend fun getJobApplications(
        employerId: String,
        jobId: String?
    ): RequestState<Flow<List<JobApplicationDetails>>>

    suspend fun getJobSeekerApplications(jobSeekerId: String): RequestState<Flow<List<JobApplicationDetails>>>
    suspend fun acceptJobSeeker(applicantId: String, jobId: String)
    suspend fun declineJobSeeker(applicantId: String, jobId: String)
    suspend fun getAcceptedEmailAddresses()
    suspend fun getDeclineEmailAddresses()
}

object JobApplicationRepoImpl : JobApplicationRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val jobApplicationCollection = firestore.collection("jobApplications")
    override suspend fun insertJobApplication(jobApplicationDetails: JobApplicationDetails): RequestState<JobApplicationDetails> {
        return try {
            jobApplicationCollection.document().set(jobApplicationDetails).await()
            RequestState.Success(jobApplicationDetails)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun getJobApplications(
        employerId: String,
        jobId: String?
    ): RequestState<Flow<List<JobApplicationDetails>>> {
        Log.d("GetJobApplications", "getJobApplications: for id $jobId")
        return try {
            val applications =
                jobApplicationCollection
                    .whereEqualTo("employerId", employerId)
                    .whereEqualTo("selectedJobId", jobId)
                    .snapshots().map {
                        it.toObjects(JobApplicationDetails::class.java)
                    }
            return RequestState.Success(applications)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun getJobSeekerApplications(jobSeekerId: String): RequestState<Flow<List<JobApplicationDetails>>> {
        return try {
            val applications =
                jobApplicationCollection.whereEqualTo("applicantId", jobSeekerId).snapshots().map {
                    it.toObjects(JobApplicationDetails::class.java)
                }
            return RequestState.Success(applications)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

    override suspend fun acceptJobSeeker(applicantId: String, jobId: String) {
        Log.d("ACCEPTED JOB SEEKER", "acceptJobSeeker: ")
        val querySnapshot = jobApplicationCollection
            .whereEqualTo("applicantId", applicantId)
            .whereEqualTo("selectedJobId", jobId)
            .get().await()
        val batch = firestore.batch()
        for (document in querySnapshot.documents) {
            val docRef = jobApplicationCollection.document(document.id)
            Log.d("ACCEPTED JOB SEEKER", "${document.id}")

            val newData = mapOf("applicationStatus" to ApplicationStatus.ACCEPTED.name)
            batch.set(docRef, newData, SetOptions.merge())
        }
        batch.commit().await()
    }

    override suspend fun declineJobSeeker(applicantId: String, jobId: String) {
        val querySnapshot = jobApplicationCollection
            .whereEqualTo("applicantId", applicantId)
            .whereEqualTo("selectedJobId", jobId)
            .get().await()
        val batch = firestore.batch()
        for (document in querySnapshot.documents) {
            val docRef = jobApplicationCollection.document(document.id)
            val newData = mapOf("applicationStatus" to ApplicationStatus.DECLINED.name)
            batch.set(docRef, newData, SetOptions.merge())
        }
        batch.commit().await()
    }

    override suspend fun getAcceptedEmailAddresses() {
        TODO("Not yet implemented")
    }

    override suspend fun getDeclineEmailAddresses() {
        TODO("Not yet implemented")
    }

}