package com.lorraine.hiremequick.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.lorraine.hiremequick.data.model.JobApplicationDetails
import com.lorraine.hiremequick.data.model.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface JobApplicationRepo {
    suspend fun insertJobApplication(jobApplicationDetails: JobApplicationDetails): RequestState<JobApplicationDetails>
    suspend fun getJobApplications(employerId: String): RequestState<Flow<List<JobApplicationDetails>>>
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

    override suspend fun getJobApplications(employerId: String): RequestState<Flow<List<JobApplicationDetails>>> {
        return try {
            val applications =
                jobApplicationCollection.whereEqualTo("employerId", employerId).snapshots().map {
                    it.toObjects(JobApplicationDetails::class.java)
                }
            return RequestState.Success(applications)
        } catch (e: Exception) {
            RequestState.Error(e)
        }
    }

}