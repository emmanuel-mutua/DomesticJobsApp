package com.lorraine.hiremequick.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lorraine.hiremequick.data.Response
import com.lorraine.hiremequick.data.model.JobPosting
import com.lorraine.hiremequick.data.model.RequestState
import com.lorraine.hiremequick.presentation.auth.EmployerData
import com.lorraine.hiremequick.presentation.auth.JobSeekerData
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

typealias AddUserResponse = Response<Boolean>
typealias JobPostings = RequestState<Map<LocalDate, List<JobPosting>>>

interface StorageService {
    suspend fun addJobSeeker(user: JobSeekerData): Boolean
    suspend fun addEmployer(user: EmployerData): Boolean
    suspend fun getUserData(uid: String, onSuccess: (DocumentSnapshot) -> Unit)
}

class StorageServiceImpl(
    private val db: FirebaseFirestore,
) : StorageService {
    override suspend fun addJobSeeker(user: JobSeekerData): Boolean {
        return try {
            db.collection("users").document(user.uid).set(user)
                .await()
            Log.d("FireStore", "User Registred success")
            true
        } catch (e: Exception) {
            Log.d("FireStore", e.localizedMessage)
            false
        }
    }

    override suspend fun addEmployer(user: EmployerData): Boolean {
        return try {
            db.collection("users").document(user.uid).set(user)
                .await()
            true
        } catch (e: Exception) {
            Log.d("FireStore", e.localizedMessage)
            false
        }
    }

    override suspend fun getUserData(
        uid: String,
        onSuccess: (DocumentSnapshot) -> Unit,
    ) {
        try {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    onSuccess(document)
                }.await()
        } catch (e: Exception) {
            Log.d("FireStore", e.localizedMessage)
        }
    }
}
