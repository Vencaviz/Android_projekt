package com.example.homework2.communication

data class CommunicationError(
    val code: Int,
    val message: String? = null
)