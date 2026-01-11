package com.projekt.xvizvary.auth.Manager

import com.projekt.xvizvary.auth.repository.UserRepository
import com.projekt.xvizvary.sync.SyncRepository
import com.projekt.xvizvary.sync.SyncResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val userRepository: UserRepository,
    private val syncRepository: SyncRepository
) {

    suspend fun isUserLoggedIn(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun getCurrentUserId() = userRepository.getCurrentUserId()

    /**
     * Syncs data from cloud after login
     */
    suspend fun syncAfterLogin(): SyncResult {
        val userId = userRepository.getCurrentUserId() ?: return SyncResult.Error("No user")
        return syncRepository.syncFromCloud(userId)
    }

    /**
     * Clears local data and logs out
     */
    suspend fun logout() {
        val userId = userRepository.getCurrentUserId()
        if (userId != null) {
            syncRepository.clearLocalData(userId)
        }
        userRepository.logout()
    }
}
