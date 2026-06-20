package fr.utbm.sy43.pilulito.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import fr.utbm.sy43.pilulito.PilulitoApplication
import fr.utbm.sy43.pilulito.data.models.Drug
import fr.utbm.sy43.pilulito.data.models.IntakeStatus
import fr.utbm.sy43.pilulito.data.models.MedicationIntake
import fr.utbm.sy43.pilulito.data.models.MedicationPlan
import fr.utbm.sy43.pilulito.data.repositories.MedicationRepository
import fr.utbm.sy43.pilulito.workers.IntakeReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

data class AddPosologyUiState(
    val drugs: List<Drug> = emptyList(),
    val selectedDrug: Drug? = null,
    // Fréquence en heures : 8h, 12h, 24h
    val frequencyHours: Int = 24,
    val quantity: Int = 1,
    // Heure de première prise
    val startHour: Int = 8,
    val startMinute: Int = 0,
    // Durée du traitement en jours
    val durationDays: Int = 7,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddPosologyViewModel(
    application: Application,
    private val medicationRepository: MedicationRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AddPosologyUiState())
    private val context: Context get() = getApplication<PilulitoApplication>()
    val uiState: StateFlow<AddPosologyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            medicationRepository.drugs.collect { drugs ->
                _uiState.value = _uiState.value.copy(drugs = drugs)
            }
        }
    }

    fun onDrugSelected(drug: Drug) {
        _uiState.value = _uiState.value.copy(selectedDrug = drug, errorMessage = null)
    }

    fun onFrequencyChanged(hours: Int) {
        _uiState.value = _uiState.value.copy(frequencyHours = hours)
    }

    fun onQuantityChanged(qty: Int) {
        if (qty >= 1) _uiState.value = _uiState.value.copy(quantity = qty)
    }

    fun onStartHourChanged(hour: Int) {
        _uiState.value = _uiState.value.copy(startHour = hour)
    }

    fun onStartMinuteChanged(minute: Int) {
        _uiState.value = _uiState.value.copy(startMinute = minute)
    }

    fun onDurationChanged(days: Int) {
        if (days >= 1) _uiState.value = _uiState.value.copy(durationDays = days)
    }

    fun scheduleNotifications(
        context: Context,
        intakes: List<MedicationIntake>,
        drugName: String,
        dosage: String
    ) {
        val workManager = WorkManager.getInstance(context)
        val now = System.currentTimeMillis()

        intakes.forEach { intake ->
            val delay = intake.realIntakeTime - now
            if (delay <= 0) return@forEach

            val data = workDataOf(
                "drug_name" to drugName,
                "dosage"    to dosage,
                "notif_id"  to intake.id.hashCode()
            )

            val request = OneTimeWorkRequestBuilder<IntakeReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("intake_${intake.id}")
                .build()

            workManager.enqueue(request)
        }
    }

    fun savePosology() {
        val state = _uiState.value
        val drug = state.selectedDrug

        if (drug == null) {
            _uiState.value = state.copy(errorMessage = "Please select a medication")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val planId     = "plan_${UUID.randomUUID()}"
            val frequencyMs = state.frequencyHours * 3_600_000L

            val startDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, state.startHour)
                set(Calendar.MINUTE, state.startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endDate = startDate + state.durationDays * 86_400_000L

            val plan = MedicationPlan(
                id        = planId,
                startDate = startDate,
                endDate   = endDate,
                frequency = frequencyMs,
                quantity  = state.quantity,
                userId    = ""
            )

            val intakes = generateIntakes(
                planId      = planId,
                drugId      = drug.id,
                startDate   = startDate,
                endDate     = endDate,
                frequencyMs = frequencyMs
            )

            medicationRepository.addPlan(plan)
            medicationRepository.addIntakes(intakes)

            scheduleNotifications(
                context  = context,
                intakes  = intakes,
                drugName = drug.name,
                dosage   = drug.dosage
            )

            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
        }
    }


    private fun generateIntakes(
        planId: String,
        drugId: String,
        startDate: Long,
        endDate: Long,
        frequencyMs: Long
    ): List<MedicationIntake> {
        val intakes = mutableListOf<MedicationIntake>()
        var time = startDate
        while (time <= endDate) {
            intakes.add(
                MedicationIntake(
                    id             = "intake_${UUID.randomUUID()}",
                    realIntakeTime = time,
                    status         = IntakeStatus.PENDING,
                    drugId         = drugId,
                    planId         = planId
                )
            )
            time += frequencyMs
        }
        return intakes
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}