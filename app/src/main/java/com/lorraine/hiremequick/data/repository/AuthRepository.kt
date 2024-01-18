package com.lorraine.hiremequick.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.lorraine.hiremequick.data.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias SignInResponse = Response<Boolean>
typealias SignOutResponse = Response<Boolean>
typealias SignUpResponse = Response<Boolean>
typealias AuthStateResponse = StateFlow<Boolean>

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signInEmailAndPassword(email: String, password: String): SignInResponse
    suspend fun signUpEmailAndPassword(email: String, password: String): SignUpResponse
    fun signOut(): SignOutResponse
    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse
    suspend fun reloadFirebaseUser()
}

class AuthAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signInEmailAndPassword(email: String, password: String): SignInResponse {
        return try {
            suspendCoroutine { continuation ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Auth", "Sign in successful")
                            continuation.resume(Response.Success(true))
                        } else {
                            task.exception?.let { exception ->
                                Log.d("Auth, Exception", "${exception.localizedMessage}")
                                continuation.resume(
                                    Response.Failure(
                                        exception.localizedMessage ?: "Unknown error",
                                    ),
                                )
                            }
                        }
                    }
            }
        } catch (e: FirebaseAuthException) {
            Log.d("Auth, FE", e.localizedMessage ?: "Firebase Exception")
            Response.Failure(e.localizedMessage ?: "Firebase Exception")
        } catch (e: Exception) {
            Log.d("Auth, E", e.localizedMessage ?: "Exception")
            Response.Failure(e.localizedMessage ?: "Exception")
        }
    }

    override suspend fun signUpEmailAndPassword(email: String, password: String): SignUpResponse {
        return try {
            suspendCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        continuation.resume(Response.Success(true))
                    }
                    .addOnFailureListener { exception ->
                        Response.Failure(
                            exception.localizedMessage ?: "Unknown error",
                        )
                    }
            }
        }catch (e : Exception){
            Response.Failure(
                e.message?:"Unknown Error"
            )
        }
    }

    override fun signOut(): SignOutResponse {
        return try {
            auth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure("E")
        }
    }

    override fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser == null)

    override suspend fun reloadFirebaseUser() {
        auth.currentUser?.reload()?.await()
    }
}
