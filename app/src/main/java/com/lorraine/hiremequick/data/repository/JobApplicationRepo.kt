package com.lorraine.hiremequick.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.presentation.jobseeker.moredetails.JobApplicationDetails
import kotlinx.coroutines.tasks.await

interface JobApplicationRepo {
    suspend fun insertJobApplication(jobApplicationDetails: JobApplicationDetails): RequestState<JobApplicationDetails>
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

}