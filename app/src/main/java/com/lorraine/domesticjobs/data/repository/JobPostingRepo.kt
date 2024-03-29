package com.lorraine.domesticjobs.data.repository

import com.lorraine.domesticjobs.data.model.JobPosting
import com.lorraine.domesticjobs.data.model.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface JobPostingRepo {
    suspend fun getAllJobsPostings(employerId : String): Flow<JobPostings>
    suspend fun getAllJobsPostings(): Flow<JobPostings>
    suspend fun getFilteredJobPostings(zonedDateTime: ZonedDateTime, studentId: String): Flow<JobPostings>
    suspend fun getSelectedJob(jobId: String): RequestState<JobPosting>
    suspend fun insertJob(jobPosting: JobPosting): RequestState<JobPosting>
    suspend fun updateJobPosting(jobPosting: JobPosting): RequestState<JobPosting>
    suspend fun deleteJobPosting(id: String): RequestState<Boolean>
    suspend fun deleteJobPosting(): RequestState<Boolean>
    suspend fun addApplicantIdToJobPosting(applicantId : String,  jobId: String): RequestState<Boolean>
}
