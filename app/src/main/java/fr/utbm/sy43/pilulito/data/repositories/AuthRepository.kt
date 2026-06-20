package fr.utbm.sy43.pilulito.data.repositories

import fr.utbm.sy43.pilulito.data.models.AccountType
import fr.utbm.sy43.pilulito.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String): User?
    suspend fun signup(
        name: String,
        surname: String,
        email: String,
        password: String,
        accountType: AccountType,
        supervisorLinkCode: String? = null  // requis si accountType == SUPERVISOR
    ): User?
    suspend fun logout()

    suspend fun findSeniorByLinkCode(linkCode: String): User?
    suspend fun updateEmail(newEmail: String)
    suspend fun updatePassword(newPassword: String)
    suspend fun deleteAccount()

    suspend fun checkExistingSession(): User?

}

class OfflineAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val users = mutableListOf(
        // senior
        User(
            id          = "user_1",
            firstName   = "Waltuh",
            lastName    = "HelpMe",
            email       = "im.a@labubu.waltuh",
            //password    = "feet",
            accountType = AccountType.SENIOR,
            linkCode    = "WALT-1234"
        ),
        // superviseur
        User(
            id             = "user_2",
            firstName      = "Jesse",
            lastName       = "Pinkman",
            email          = "jesse@supervisor.com",
            //password       = "yo",
            accountType    = AccountType.SUPERVISOR,
            linkCode       = "JESSE-5678",
            linkedSeniorId = "user_1"
        )
    )

    override suspend fun deleteAccount() {
        val user = _currentUser.value ?: return
        users.removeAll { it.id == user.id }
        _currentUser.value = null
    }
    //now password is sent in fireAuth, error with this old function
    override suspend fun login(email: String, password: String): User? {
        return null
        /*
        val user = users.find { it.email == email && it.password == password }
        _currentUser.value = user
        return user
        */

    }

    override suspend fun signup(
        name: String,
        surname: String,
        email: String,
        password: String,
        accountType: AccountType,
        supervisorLinkCode: String?
    ): User? {
        if (users.any { it.email == email }) return null

        var linkedSeniorId: String? = null

        if (accountType == AccountType.SUPERVISOR) {
            if (supervisorLinkCode.isNullOrBlank()) return null
            val senior = users.find {
                it.accountType == AccountType.SENIOR && it.linkCode == supervisorLinkCode
            } ?: return null
            linkedSeniorId = senior.id
        }

        val newUser = User(
            id             = UUID.randomUUID().toString(),
            firstName      = name,
            lastName       = surname,
            email          = email,
            //password       = password,
            accountType    = accountType,
            linkCode       = generateLinkCode(name),
            linkedSeniorId = linkedSeniorId
        )
        users.add(newUser)
        return newUser
    }

    override suspend fun updateEmail(newEmail: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(email = newEmail)
        users.replaceAll { if (it.id == user.id) updated else it }
        _currentUser.value = updated
    }

    override suspend fun updatePassword(newPassword: String) {
        return
        /*
        val user = _currentUser.value ?: return
        val updated = user.copy(password = newPassword)
        users.replaceAll { if (it.id == user.id) updated else it }
        _currentUser.value = updated
        */

    }

    override suspend fun findSeniorByLinkCode(linkCode: String): User? {
        return users.find {
            it.accountType == AccountType.SENIOR && it.linkCode == linkCode
        }
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    private fun generateLinkCode(name: String): String {
        val prefix = name.take(4).uppercase().ifBlank { "USER" }
        val suffix = (1000..9999).random()
        return "$prefix-$suffix"
    }

    //just to implement interface
    override suspend fun checkExistingSession(): User?{
        return  null
    }
}