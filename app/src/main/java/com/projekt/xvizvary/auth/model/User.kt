package com.projekt.xvizvary.auth.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null
)
