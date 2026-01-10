package com.projekt.xvizvary.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.projekt.xvizvary.auth.Manager.PreferencesManager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val preferencesManager: PreferencesManager
) {

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                preferencesManager.saveUserSession(user.uid, user.email ?: "")
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                preferencesManager.saveUserSession(user.uid, user.email ?: "")
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { user ->
                preferencesManager.saveUserSession(user.uid, user.email ?: "")
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Boolean> {
        return try {
            firebaseAuth.signOut()
            preferencesManager.clearUserSession()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUserAuthenticated(): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        val isLoggedInFlow = preferencesManager.isUserLoggedIn()
        val isLoggedIn = isLoggedInFlow.map { it }.let { flow ->
            var result = false
            flow.collect { value ->
                result = value
            }
            result
        }

        return firebaseUser != null && isLoggedIn
    }
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
