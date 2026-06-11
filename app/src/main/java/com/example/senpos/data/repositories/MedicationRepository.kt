package com.example.senpos.data.repositories

import com.example.senpos.data.models.Drug
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.data.models.MedicationIntake
import com.example.senpos.data.models.MedicationPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

interface MedicationRepository {
    val intakes: StateFlow<List<MedicationIntake>>
    val drugs: StateFlow<List<Drug>>
    val plans: StateFlow<List<MedicationPlan>>
    
    suspend fun updateIntakeStatus(intakeId: String, status: IntakeStatus)
}

class OfflineMedicationRepository : MedicationRepository {

    private val _drugs = MutableStateFlow(listOf(
        Drug(id = "drug_1", name = "Doliprane", dosage = "1000mg", form = "Tablet"),
        Drug(id = "drug_2", name = "Metformine", dosage = "500mg", form = "Tablet"),
        Drug(id = "drug_3", name = "Amlodipine", dosage = "5mg", form = "Tablet"),
        Drug(id = "drug_4", name = "Omeprazole", dosage = "20mg", form = "Capsule")
    ))
    override val drugs: StateFlow<List<Drug>> = _drugs.asStateFlow()

    private val _plans = MutableStateFlow(listOf(
        MedicationPlan(id = "plan_1", startDate = daysAgoAt(10, 8), endDate = daysAgoAt(-20, 8), frequency = 28_800_000L,  quantity = 1, userId = "user_1"),
        MedicationPlan(id = "plan_2", startDate = daysAgoAt(5, 8),  endDate = daysAgoAt(-25, 8), frequency = 43_200_000L,  quantity = 1, userId = "user_1"),
        MedicationPlan(id = "plan_3", startDate = daysAgoAt(3, 9),  endDate = daysAgoAt(-27, 9), frequency = 86_400_000L,  quantity = 2, userId = "user_1"),
        MedicationPlan(id = "plan_4", startDate = daysAgoAt(7, 7),  endDate = daysAgoAt(-7, 7),  frequency = 86_400_000L,  quantity = 1, userId = "user_1")
    ))
    override val plans: StateFlow<List<MedicationPlan>> = _plans.asStateFlow()

    private val _intakes = MutableStateFlow(listOf(
        MedicationIntake(id = "t1", realIntakeTime = todayAt(7), status = IntakeStatus.PENDING, drugId = "drug_1", planId = "plan_1"),
        MedicationIntake(id = "t2", realIntakeTime = todayAt(15), status = IntakeStatus.PENDING, drugId = "drug_1", planId = "plan_1"),
        MedicationIntake(id = "t3", realIntakeTime = todayAt(23), status = IntakeStatus.PENDING, drugId = "drug_1", planId = "plan_1"),
        MedicationIntake(id = "t4", realIntakeTime = todayAt(8), status = IntakeStatus.PENDING, drugId = "drug_2", planId = "plan_2"),
        MedicationIntake(id = "t5", realIntakeTime = todayAt(20), status = IntakeStatus.PENDING, drugId = "drug_2", planId = "plan_2"),
        MedicationIntake(id = "t6", realIntakeTime = todayAt(9), status = IntakeStatus.PENDING, drugId = "drug_3", planId = "plan_3"),
        MedicationIntake(id = "t7", realIntakeTime = todayAt(7, 30), status = IntakeStatus.PENDING, drugId = "drug_4", planId = "plan_4"),
        MedicationIntake(id = "o1", realIntakeTime = daysAgoAt(1, 7), status = IntakeStatus.PENDING, drugId = "drug_1", planId = "plan_1"),
        MedicationIntake(id = "o2", realIntakeTime = daysAgoAt(1, 15), status = IntakeStatus.PENDING, drugId = "drug_1", planId = "plan_1"),
        MedicationIntake(id = "o3", realIntakeTime = daysAgoAt(1, 8), status = IntakeStatus.PENDING, drugId = "drug_2", planId = "plan_2"),
        MedicationIntake(id = "o4", realIntakeTime = daysAgoAt(2, 9), status = IntakeStatus.PENDING, drugId = "drug_3", planId = "plan_3")
    ))
    override val intakes: StateFlow<List<MedicationIntake>> = _intakes.asStateFlow()

    override suspend fun updateIntakeStatus(intakeId: String, status: IntakeStatus) {
        _intakes.value = _intakes.value.map {
            if (it.id == intakeId) it.copy(status = status) else it
        }
    }

    private fun todayAt(hour: Int, minute: Int = 0): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun daysAgoAt(days: Int, hour: Int): Long =
        todayAt(hour) - days * 24 * 60 * 60 * 1000L
}
