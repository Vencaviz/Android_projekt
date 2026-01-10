package com.projekt.xvizvary.auth.Manager

import com.projekt.xvizvary.auth.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val userRepository: UserRepository
) {


    suspend fun isUserLoggedIn(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    fun getCurrentUser() = userRepository.getCurrentUser()

    suspend fun logout() = userRepository.logout()
}
