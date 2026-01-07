package com.example.homework2.ui.auth.register

import androidx.lifecycle.ViewModel
import com.example.homework2.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    fun logout() = repository.signOut()
}