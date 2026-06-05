package com.example.senpos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.senpos.data.models.Drug
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.data.models.MedicationIntake
import com.example.senpos.data.models.MedicationPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class IntakeCardUiModel(
    val intakeId: String,
    val drugName: String,
    val dosage: String,
    val form: String,
    val quantity: Int,
    val scheduledTime: Long,
    val status: IntakeStatus,
    val isUpcoming: Boolean
)

data class OverdueIntakeUiModel(
    val intakeId: String,
    val drugName: String,
    val dosage: String,
    val form: String,
    val quantity: Int,
    val scheduledTime: Long
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val todayIntakes: List<IntakeCardUiModel> = emptyList(),
    val overdueIntakes: List<OverdueIntakeUiModel> = emptyList(),
    val showOverdueDialog: Boolean = false,
    val errorMessage: String? = null
)


private fun todayAt(hour: Int, minute: Int = 0): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun daysAgoAt(days: Int, hour: Int): Long =
    todayAt(hour) - days * 24 * 60 * 60 * 1000L

private val dummyDrugs = listOf(
    Drug(id = "drug_1", name = "Doliprane", dosage = "1000mg", form = "Tablet"),
    Drug(id = "drug_2", name = "Metformine", dosage = "500mg", form = "Tablet"),
    Drug(id = "drug_3", name = "Amlodipine", dosage = "5mg", form = "Tablet"),
    Drug(id = "drug_4", name = "Omeprazole", dosage = "20mg", form = "Capsule")
)

private val dummyPlans = listOf(
    MedicationPlan(id = "plan_1", startDate = daysAgoAt(10, 8), endDate = daysAgoAt(-20, 8), frequency = 28_800_000L,  quantity = 1, userId = "user_1"), // toutes les 8h
    MedicationPlan(id = "plan_2", startDate = daysAgoAt(5, 8),  endDate = daysAgoAt(-25, 8), frequency = 43_200_000L,  quantity = 1, userId = "user_1"), // toutes les 12h
    MedicationPlan(id = "plan_3", startDate = daysAgoAt(3, 9),  endDate = daysAgoAt(-27, 9), frequency = 86_400_000L,  quantity = 2, userId = "user_1"), // toutes les 24h
    MedicationPlan(id = "plan_4", startDate = daysAgoAt(7, 7),  endDate = daysAgoAt(-7, 7),  frequency = 86_400_000L,  quantity = 1, userId = "user_1")  // toutes les 24h
)

private val dummyIntakes = mutableListOf(

    // Doliprane 3x/jour → 07:00 / 15:00 / 23:00
    MedicationIntake(
        id = "t1",
        realIntakeTime = todayAt(7),
        status = IntakeStatus.PENDING,
        drugId = "drug_1",
        planId = "plan_1"
    ),
    MedicationIntake(
        id = "t2",
        realIntakeTime = todayAt(15),
        status = IntakeStatus.PENDING,
        drugId = "drug_1",
        planId = "plan_1"
    ),
    MedicationIntake(
        id = "t3",
        realIntakeTime = todayAt(23),
        status = IntakeStatus.PENDING,
        drugId = "drug_1",
        planId = "plan_1"
    ),
    // Metformine 2x/jour → 08:00 / 20:00
    MedicationIntake(
        id = "t4",
        realIntakeTime = todayAt(8),
        status = IntakeStatus.PENDING,
        drugId = "drug_2",
        planId = "plan_2"
    ),
    MedicationIntake(
        id = "t5",
        realIntakeTime = todayAt(20),
        status = IntakeStatus.PENDING,
        drugId = "drug_2",
        planId = "plan_2"
    ),
    // Amlodipine 1x/jour → 09:00
    MedicationIntake(
        id = "t6",
        realIntakeTime = todayAt(9),
        status = IntakeStatus.PENDING,
        drugId = "drug_3",
        planId = "plan_3"
    ),
    // Omeprazole 1x/jour → 07:30
    MedicationIntake(
        id = "t7",
        realIntakeTime = todayAt(7, 30),
        status = IntakeStatus.PENDING,
        drugId = "drug_4",
        planId = "plan_4"
    ),


    MedicationIntake(
        id = "o1",
        realIntakeTime = daysAgoAt(1, 7),
        status = IntakeStatus.PENDING,
        drugId = "drug_1",
        planId = "plan_1"
    ),
    MedicationIntake(
        id = "o2",
        realIntakeTime = daysAgoAt(1, 15),
        status = IntakeStatus.PENDING,
        drugId = "drug_1",
        planId = "plan_1"
    ),
    MedicationIntake(
        id = "o3",
        realIntakeTime = daysAgoAt(1, 8),
        status = IntakeStatus.PENDING,
        drugId = "drug_2",
        planId = "plan_2"
    ),
    MedicationIntake(
        id = "o4",
        realIntakeTime = daysAgoAt(2, 9),
        status = IntakeStatus.PENDING,
        drugId = "drug_3",
        planId = "plan_3"
    ),
)


class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val intakes = dummyIntakes

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startOfDay = todayAt(0)
            val endOfDay = todayAt(23, 59)

            val todayCards = intakes
                .filter { it.realIntakeTime in startOfDay..endOfDay }
                .mapNotNull { intake -> buildTodayCard(intake, now) }
                .sortedBy { it.scheduledTime }

            val overdueCards = intakes
                .filter { it.realIntakeTime < startOfDay && it.status == IntakeStatus.PENDING }
                .mapNotNull { intake -> buildOverdueCard(intake) }
                .sortedByDescending { it.scheduledTime }

            _uiState.value = HomeUiState(
                todayIntakes = todayCards,
                overdueIntakes = overdueCards,
                showOverdueDialog = overdueCards.isNotEmpty()
            )
        }
    }

    private fun buildTodayCard(intake: MedicationIntake, now: Long): IntakeCardUiModel? {
        val drug = dummyDrugs.find { it.id == intake.drugId } ?: return null
        val plan = dummyPlans.find { it.id == intake.planId } ?: return null
        return IntakeCardUiModel(
            intakeId = intake.id,
            drugName = drug.name,
            dosage = drug.dosage,
            form = drug.form,
            quantity = plan.quantity,
            scheduledTime = intake.realIntakeTime,
            status = intake.status,
            isUpcoming = intake.realIntakeTime > now && intake.status == IntakeStatus.PENDING
        )
    }

    private fun buildOverdueCard(intake: MedicationIntake): OverdueIntakeUiModel? {
        val drug = dummyDrugs.find { it.id == intake.drugId } ?: return null
        val plan = dummyPlans.find { it.id == intake.planId } ?: return null
        return OverdueIntakeUiModel(
            intakeId = intake.id,
            drugName = drug.name,
            dosage = drug.dosage,
            form = drug.form,
            quantity = plan.quantity,
            scheduledTime = intake.realIntakeTime
        )
    }


    /** Bouton "I took it" sur une carte du jour */
    fun markAsTaken(intakeId: String) {
        updateStatus(intakeId, IntakeStatus.TAKEN)
        refreshTodayIntakes()
    }

    /** Bouton "I took it" sur une carte en retard */
    fun markOverdueAsTaken(intakeId: String) {
        updateStatus(intakeId, IntakeStatus.TAKEN)
        refreshOverdueIntakes()
    }

    /** Bouton "I missed it" sur une carte en retard */
    fun markOverdueAsMissed(intakeId: String) {
        updateStatus(intakeId, IntakeStatus.MISSED)
        refreshOverdueIntakes()
    }

    fun dismissOverdueDialog() {
        _uiState.value = _uiState.value.copy(showOverdueDialog = false)
    }


    private fun updateStatus(intakeId: String, status: IntakeStatus) {
        intakes.find { it.id == intakeId }?.status = status
    }

    private fun refreshTodayIntakes() {
        val now = System.currentTimeMillis()
        val startOfDay = todayAt(0)
        val endOfDay = todayAt(23, 59)
        val updated = intakes
            .filter { it.realIntakeTime in startOfDay..endOfDay }
            .mapNotNull { buildTodayCard(it, now) }
            .sortedBy { it.scheduledTime }
        _uiState.value = _uiState.value.copy(todayIntakes = updated)
    }

    private fun refreshOverdueIntakes() {
        val startOfDay = todayAt(0)
        val updated = intakes
            .filter { it.realIntakeTime < startOfDay && it.status == IntakeStatus.PENDING }
            .mapNotNull { buildOverdueCard(it) }
            .sortedByDescending { it.scheduledTime }
        _uiState.value = _uiState.value.copy(
            overdueIntakes = updated,
            showOverdueDialog = updated.isNotEmpty()
        )
    }
}