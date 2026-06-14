package com.example.senpos.data.models

enum class AccountType {
    SENIOR,
    SUPERVISOR
}

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val accountType: AccountType,
    val linkCode: String,
    val linkedSeniorId: String? = null
)