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
    private val firestore: FirebaseFirestore
) : MedicationRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    //references to firestore collections
    private val drugsCollection = firestore.collection("drugs")
    private val plansCollection = firestore.collection("plans")
    private val intakesCollection = firestore.collection("intakes")

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
        val listener = plansCollection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.toObjects(MedicationPlan::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }.stateIn(repositoryScope, SharingStarted.WhileSubscribed(5000), emptyList())

    override val intakes: StateFlow<List<MedicationIntake>> = callbackFlow {
        val listener = intakesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val list = snapshot?.toObjects(MedicationIntake::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }.stateIn(repositoryScope, SharingStarted.WhileSubscribed(5000), emptyList())


    //request to the distant database
    override suspend fun updateIntakeStatus(intakeId: String, status: IntakeStatus) {
        try {
            intakesCollection.document(intakeId)
                .update("status", status.name)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addPlan(plan: MedicationPlan) {
        try {
            val id = plan.id.ifEmpty { plansCollection.document().id }
            val planToSave = if (plan.id.isEmpty()) plan.copy(id = id) else plan

            plansCollection.document(id).set(planToSave).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addIntakes(intakes: List<MedicationIntake>) {
        try {
            intakes.forEach { intake ->
                val id = intake.id.ifEmpty { intakesCollection.document().id }
                val intakeToSave = if (intake.id.isEmpty()) intake.copy(id = id) else intake

                intakesCollection.document(id)
                    .set(intakeToSave)
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}