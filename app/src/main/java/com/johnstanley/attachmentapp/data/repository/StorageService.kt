package com.johnstanley.attachmentapp.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstanley.attachmentapp.data.Response
import com.johnstanley.attachmentapp.data.model.AttachmentLog
import com.johnstanley.attachmentapp.data.model.RequestState
import com.johnstanley.attachmentapp.presentation.auth.StaffData
import com.johnstanley.attachmentapp.presentation.auth.StudentData
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

typealias AddUserResponse = Response<Boolean>
typealias AttachmentLogs = RequestState<Map<LocalDate, List<AttachmentLog>>>

interface StorageService {
    suspend fun addStudent(user: StudentData): Boolean
    suspend fun addStaff(user: StaffData): Boolean
    suspend fun getUserData(uid: String, onSuccess: (DocumentSnapshot) -> Unit)
}

class StorageServiceImpl(
    private val db: FirebaseFirestore,
) : StorageService {
    override suspend fun addStudent(user: StudentData): Boolean {
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

    override suspend fun addStaff(user: StaffData): Boolean {
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
