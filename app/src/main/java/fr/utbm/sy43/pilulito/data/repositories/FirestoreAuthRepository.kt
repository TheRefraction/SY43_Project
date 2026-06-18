package fr.utbm.sy43.pilulito.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import fr.utbm.sy43.pilulito.data.models.AccountType
import fr.utbm.sy43.pilulito.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreAuthRepository(
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val usersCollection = firestore.collection("users")

    override suspend fun login(email: String, password: String): User? {
        try {
            //look for the right user
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()

            val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
            _currentUser.value = user
            return user
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun signup(
        name: String,
        surname: String,
        email: String,
        password: String,
        accountType: AccountType,
        supervisorLinkCode: String?
    ): User? {
        try {
            //email must be unique
            val emailCheck = usersCollection.whereEqualTo("email", email).get().await()
            if (!emailCheck.isEmpty) return null

            var linkedSeniorId: String? = null

            //if supervisor then look for the senior's code
            if (accountType == AccountType.SUPERVISOR) {
                if (supervisorLinkCode.isNullOrBlank()) return null
                val senior = findSeniorByLinkCode(supervisorLinkCode) ?: return null
                linkedSeniorId = senior.id
            }

            val userId = UUID.randomUUID().toString()
            val newUser = User(
                id             = userId,
                firstName      = name,
                lastName       = surname,
                email          = email,
                password       = password,
                accountType    = accountType,
                linkCode       = generateLinkCode(name),
                linkedSeniorId = linkedSeniorId
            )

            //save in firestore
            usersCollection.document(userId).set(newUser).await()
            _currentUser.value = newUser
            return newUser
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun findSeniorByLinkCode(linkCode: String): User? {
        try {
            val snapshot = usersCollection
                .whereEqualTo("accountType", AccountType.SENIOR.name)
                .whereEqualTo("linkCode", linkCode)
                .get()
                .await()

            return snapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
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
}