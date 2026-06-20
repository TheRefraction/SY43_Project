package fr.utbm.sy43

import fr.utbm.sy43.pilulito.data.models.Drug
import fr.utbm.sy43.pilulito.data.models.IntakeStatus
import fr.utbm.sy43.pilulito.data.models.MedicationIntake
import fr.utbm.sy43.pilulito.data.models.MedicationPlan
import fr.utbm.sy43.pilulito.data.repositories.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMedicationRepository : MedicationRepository {
    private val _drugs = MutableStateFlow<List<Drug>>(emptyList())
    override val drugs: StateFlow<List<Drug>> = _drugs.asStateFlow()

    private val _plans = MutableStateFlow<List<MedicationPlan>>(emptyList())
    override val plans: StateFlow<List<MedicationPlan>> = _plans.asStateFlow()

    private val _intakes = MutableStateFlow<List<MedicationIntake>>(emptyList())
    override val intakes: StateFlow<List<MedicationIntake>> = _intakes.asStateFlow()

    var lastAddedPlan: MedicationPlan? = null
    var lastAddedIntakes: List<MedicationIntake>? = null

    fun setDrugs(drugs: List<Drug>) { _drugs.value = drugs }

    override suspend fun updateIntakeStatus(intakeId: String, status: IntakeStatus) {
        _intakes.value = _intakes.value.map {
            if (it.id == intakeId) it.copy(status = status) else it
        }
    }

    override suspend fun addPlan(plan: MedicationPlan) {
        lastAddedPlan = plan
        _plans.value += plan
    }

    override suspend fun addIntakes(intakes: List<MedicationIntake>) {
        lastAddedIntakes = intakes
        _intakes.value += intakes
    }
}