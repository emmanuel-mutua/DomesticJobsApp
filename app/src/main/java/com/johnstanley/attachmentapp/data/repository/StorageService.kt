package com.johnstanley.attachmentapp.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstanley.attachmentapp.data.Response
import com.johnstanley.attachmentapp.presentation.auth.StaffData
import com.johnstanley.attachmentapp.presentation.auth.StudentData
import kotlinx.coroutines.tasks.await

typealias AddUserResponse = Response<Boolean>

sealed class UserData {
    data class Student(val data: StudentData) : UserData()
    data class Staff(val data: StaffData) : UserData()
}

interface StorageService {
    suspend fun addStudent(user: StudentData): Boolean
    suspend fun addStaff(user: StaffData): Boolean
    suspend fun getUserData(uid: String, collection: String, onSuccess: (DocumentSnapshot) -> Unit)
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
        collection: String,
        onSuccess: (DocumentSnapshot) -> Unit,
    ) {
        try {
            db.collection(collection).document(uid).get()
                .addOnSuccessListener { document ->
                    onSuccess(document)
                }.await()
        } catch (e: Exception) {
            Log.d("FireStore", e.localizedMessage)
        }
    }
}
