package fr.utbm.sy43.pilulito.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import fr.utbm.sy43.pilulito.data.models.Drug
import fr.utbm.sy43.pilulito.data.models.IntakeStatus
import fr.utbm.sy43.pilulito.data.models.MedicationIntake
import fr.utbm.sy43.pilulito.data.models.MedicationPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class FirestoreMedicationRepository(
    private val firestore: FirebaseFirestore,
    private val getUserId: () -> String
) : MedicationRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)


    //references to firestore collections
    private val drugsCollection = firestore.collection("drugs")

    //listeners to update in real time
    override val drugs: StateFlow<List<Drug>> = callbackFlow {
        val listener = drugsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.toObjects(Drug::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }.stateIn(repositoryScope, SharingStarted.WhileSubscribed(5000), emptyList())


    override val plans: StateFlow<List<MedicationPlan>> = callbackFlow {
        val userId = getUserId()
        val listener = firestore.collection("plans")
            .document(userId)
            .collection("user_plans")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.toObjects(MedicationPlan::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }.stateIn(repositoryScope, SharingStarted.WhileSubscribed(5000), emptyList())


    override val intakes: StateFlow<List<MedicationIntake>> = callbackFlow {
        val userId = getUserId()
        val listener =firestore.collection("intakes")
            .document(userId)
            .collection("user_intakes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val list = snapshot?.toObjects(MedicationIntake::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }.stateIn(repositoryScope, SharingStarted.WhileSubscribed(5000), emptyList())

    
    override suspend fun updateIntakeStatus(intakeId: String, status: IntakeStatus) {
        try {
            val userId = getUserId()
            firestore.collection("intakes")
                .document(userId)
                .collection("user_intakes")
                .document(intakeId)
                .update("status", status.name)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addPlan(plan: MedicationPlan) {
        try {
            val userId = if (plan.userId.isNullOrEmpty()) getUserId() else plan.userId
            if (userId.isBlank()) return

            val userPlansCollection = firestore.collection("plans")
                .document(userId)
                .collection("user_plans")

            val id = if (plan.id.isNullOrEmpty()) {
                "plan_${java.util.UUID.randomUUID()}"
            } else {
                plan.id
            }

            val planToSave = plan.copy(id = id, userId = userId)

            userPlansCollection.document(id).set(planToSave).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addIntakes(intakes: List<MedicationIntake>) {
        try {
            val userId = getUserId()
            if (userId.isBlank()) return

            val userIntakesCollection = firestore.collection("intakes")
                .document(userId)
                .collection("user_intakes")

            intakes.forEach { intake ->
                val id = if (intake.id.isNullOrEmpty()) {
                    "intake_${java.util.UUID.randomUUID()}"
                } else {
                    intake.id
                }

                val intakeToSave = intake.copy(id = id)

                userIntakesCollection.document(id)
                    .set(intakeToSave)
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}