package com.example.senpos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.senpos.data.models.Drug
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.data.models.MedicationIntake
import com.example.senpos.data.models.MedicationPlan
import com.example.senpos.data.repositories.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

class HomeViewModel(private val medicationRepository: MedicationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                medicationRepository.intakes,
                medicationRepository.drugs,
                medicationRepository.plans
            ) { intakes, drugs, plans ->
                val now = System.currentTimeMillis()
                val startOfDay = getStartOfDay()
                val endOfDay = getEndOfDay()

                val todayCards = intakes
                    .filter { it.realIntakeTime in startOfDay..endOfDay }
                    .mapNotNull { intake -> buildTodayCard(intake, drugs, plans, now) }
                    .sortedBy { it.scheduledTime }

                val overdueCards = intakes
                    .filter { it.realIntakeTime < startOfDay && it.status == IntakeStatus.PENDING }
                    .mapNotNull { intake -> buildOverdueCard(intake, drugs, plans) }
                    .sortedByDescending { it.scheduledTime }

                HomeUiState(
                    todayIntakes = todayCards,
                    overdueIntakes = overdueCards,
                    showOverdueDialog = overdueCards.isNotEmpty(),
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun buildTodayCard(
        intake: MedicationIntake,
        drugs: List<Drug>,
        plans: List<MedicationPlan>,
        now: Long
    ): IntakeCardUiModel? {
        val drug = drugs.find { it.id == intake.drugId } ?: return null
        val plan = plans.find { it.id == intake.planId } ?: return null
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

    private fun buildOverdueCard(
        intake: MedicationIntake,
        drugs: List<Drug>,
        plans: List<MedicationPlan>
    ): OverdueIntakeUiModel? {
        val drug = drugs.find { it.id == intake.drugId } ?: return null
        val plan = plans.find { it.id == intake.planId } ?: return null
        return OverdueIntakeUiModel(
            intakeId = intake.id,
            drugName = drug.name,
            dosage = drug.dosage,
            form = drug.form,
            quantity = plan.quantity,
            scheduledTime = intake.realIntakeTime
        )
    }

    fun markAsTaken(intakeId: String) {
        viewModelScope.launch {
            medicationRepository.updateIntakeStatus(intakeId, IntakeStatus.TAKEN)
        }
    }

    fun markOverdueAsTaken(intakeId: String) {
        viewModelScope.launch {
            medicationRepository.updateIntakeStatus(intakeId, IntakeStatus.TAKEN)
        }
    }

    fun markOverdueAsMissed(intakeId: String) {
        viewModelScope.launch {
            medicationRepository.updateIntakeStatus(intakeId, IntakeStatus.MISSED)
        }
    }

    fun dismissOverdueDialog() {
        _uiState.value = _uiState.value.copy(showOverdueDialog = false)
    }

    private fun getStartOfDay(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getEndOfDay(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
