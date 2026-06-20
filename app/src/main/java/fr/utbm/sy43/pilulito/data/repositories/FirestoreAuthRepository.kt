package fr.utbm.sy43.pilulito.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import fr.utbm.sy43.pilulito.data.models.AccountType
import fr.utbm.sy43.pilulito.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreAuthRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    private val usersCollection = firestore.collection("users")

    //check if user was logged on
    override suspend fun checkExistingSession(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        try {
            val snapshot = usersCollection.document(firebaseUser.uid).get().await()
            val user = snapshot.toObject(User::class.java)
            _currentUser.value = user
            return user
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }




    override suspend fun login(email: String, password: String): User? {
        try {
            //check email and password
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()


            val firebaseUserId = authResult.user?.uid ?: return null

            // if email and password correct, then get all the other information from firestore
            val snapshot = usersCollection.document(firebaseUserId).get().await()
            val user = snapshot.toObject(User::class.java)

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
            var linkedSeniorId: String? = null

            //if supervisor, look for the senior's code
            if (accountType == AccountType.SUPERVISOR) {
                if (supervisorLinkCode.isNullOrBlank()) return null
                val senior = findSeniorByLinkCode(supervisorLinkCode) ?: return null
                linkedSeniorId = senior.id
            }

            //account creation
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            //get id from firebase auth
            val firebaseUserId = authResult.user?.uid ?: return null

            //set data for firestore
            val newUser = User(
                id             = firebaseUserId, //id from firebase auth
                firstName      = name,
                lastName       = surname,
                email          = email,
                accountType    = accountType,
                linkCode       = generateLinkCode(name),
                linkedSeniorId = linkedSeniorId
            )

            usersCollection.document(firebaseUserId).set(newUser).await()
            //update local session
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
        firebaseAuth.signOut()
        _currentUser.value = null
    }

    override suspend fun updateEmail(newEmail: String) {
        try {
            val userId = _currentUser.value?.id ?: return

            //update in fireAuth
            //updateEmail IS DEPRECATED AND NOT WORKING
            firebaseAuth.currentUser?.updateEmail(newEmail)?.await()

            //update in firestore
            usersCollection.document(userId).update("email", newEmail).await()

            _currentUser.value = _currentUser.value?.copy(email = newEmail)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            //update in fireauth
            firebaseAuth.currentUser?.updatePassword(newPassword)?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteAccount() {
        try {
            val userId = _currentUser.value?.id ?: return
            usersCollection.document(userId).delete().await()
            firebaseAuth.currentUser?.delete()?.await()
            _currentUser.value = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateLinkCode(name: String): String {
        val prefix = name.take(4).uppercase().ifBlank { "USER" }
        val suffix = (1000..9999).random()
        return "$prefix-$suffix"
    }
}