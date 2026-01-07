package com.example.homework2.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


@Singleton // Zajistí, že v celé aplikaci bude jen jedna instance
class AuthRepository @Inject constructor(
    // Pokud i FirebaseAuth poskytuješ přes modul, Hilt ho sem vloží
    private val firebaseAuth: FirebaseAuth
) {

    // Sledujeme stav přihlášení jako Flow (proud dat)
    // Pokud se uživatel přihlásí nebo odhlásí, Flow okamžitě vyšle novou hodnotu
    val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            // Nabídneme aktuálního uživatele do streamu
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)

        // Když se Flow přestane používat, listener odpojíme (prevence memory leaků)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    // Funkce pro přihlášení
    suspend fun signIn(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            result.user?.let { Result.success(it) } ?: Result.failure(Exception("Uživatel nenalezen"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funkce pro odhlášení
    fun signOut() {
        firebaseAuth.signOut()
    }
}