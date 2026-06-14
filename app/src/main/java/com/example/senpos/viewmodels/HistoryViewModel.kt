package com.example.senpos.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.data.repositories.MedicationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class DayStats(
    val label: String,       // "Mon", "Tue", etc.
    val dateMillis: Long,
    val taken: Int,
    val missed: Int,
    val pending: Int
) {
    val total: Int get() = taken + missed + pending
    val takenRatio: Float get() = if (total == 0) 0f else taken.toFloat() / total
}

data class DrugStat(
    val drugName: String,
    val dosage: String,
    val taken: Int,
    val missed: Int,
    val pending: Int
) {
    val total: Int get() = taken + missed + pending
    val takenRatio: Float get() = if (total == 0) 0f else taken.toFloat() / total
}

data class HistoryUiState(
    val isLoading: Boolean = true,
    val weekStats: List<DayStats> = emptyList(),   // 7 derniers jours
    val drugStats: List<DrugStat> = emptyList(),   // par médicament
    val globalTaken: Int = 0,
    val globalTotal: Int = 0
)

class HistoryViewModel(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                medicationRepository.intakes,
                medicationRepository.drugs
            ) { intakes, drugs ->

                val now        = System.currentTimeMillis()
                val startOfToday = getStartOfDay(0)

                val weekStats = (6 downTo 0).map { daysAgo ->
                    val dayStart = getStartOfDay(daysAgo)
                    val dayEnd   = dayStart + 86_400_000L - 1L

                    val dayIntakes = intakes.filter { it.realIntakeTime in dayStart..dayEnd }

                    DayStats(
                        label      = getDayLabel(daysAgo),
                        dateMillis = dayStart,
                        taken      = dayIntakes.count { it.status == IntakeStatus.TAKEN },
                        missed     = dayIntakes.count { it.status == IntakeStatus.MISSED },
                        pending    = dayIntakes.count {
                            it.status == IntakeStatus.PENDING
                                    && it.realIntakeTime < now    // seulement les passés
                        }
                    )
                }

                val weekStart = getStartOfDay(6)
                val drugStats = drugs.map { drug ->
                    val drugIntakes = intakes.filter {
                        it.drugId == drug.id && it.realIntakeTime in weekStart..now
                    }
                    DrugStat(
                        drugName  = drug.name,
                        dosage    = drug.dosage,
                        taken     = drugIntakes.count { it.status == IntakeStatus.TAKEN },
                        missed    = drugIntakes.count { it.status == IntakeStatus.MISSED },
                        pending   = drugIntakes.count {
                            it.status == IntakeStatus.PENDING && it.realIntakeTime < now
                        }
                    )
                }.filter { it.total > 0 }  // ignore les médicaments sans prise cette semaine

                val pastIntakes = intakes.filter { it.realIntakeTime < now }

                HistoryUiState(
                    isLoading    = false,
                    weekStats    = weekStats,
                    drugStats    = drugStats,
                    globalTaken  = pastIntakes.count { it.status == IntakeStatus.TAKEN },
                    globalTotal  = pastIntakes.size
                )
            }.collect { _uiState.value = it }
        }
    }

    private fun getStartOfDay(daysAgo: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, -daysAgo)
        }.timeInMillis

    private fun getDayLabel(daysAgo: Int): String {
        if (daysAgo == 0) return "Today"
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        return listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }
}