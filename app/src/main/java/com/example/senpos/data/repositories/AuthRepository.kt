package com.example.senpos.data.repositories

import com.example.senpos.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String): User?
    suspend fun signup(name: String, surname: String, email: String, password: String): User?
    suspend fun logout()
}

class OfflineAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val users = mutableListOf(
        User(
            id = "user_1",
            firstName = "Waltuh",
            lastName = "HelpMe",
            email = "im.a@labubu.waltuh",
            password = "feet"
        )
    )

    override suspend fun login(email: String, password: String): User? {
        val user = users.find { it.email == email && it.password == password }
        _currentUser.value = user
        return user
    }

    override suspend fun signup(name: String, surname: String, email: String, password: String): User? {
        if (users.any { it.email == email }) return null
        
        val newUser = User(
            id = java.util.UUID.randomUUID().toString(),
            firstName = name,
            lastName = surname,
            email = email,
            password = password
        )
        users.add(newUser)
        return newUser
    }

    override suspend fun logout() {
        _currentUser.value = null
    }
}
