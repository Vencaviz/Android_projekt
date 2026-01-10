package com.projekt.xvizvary.auth

import com.projekt.xvizvary.auth.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        userRepository.register(email, password)
}
