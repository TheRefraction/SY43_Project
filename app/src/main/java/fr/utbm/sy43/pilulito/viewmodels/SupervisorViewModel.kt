package fr.utbm.sy43.pilulito.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.utbm.sy43.pilulito.data.models.IntakeStatus
import fr.utbm.sy43.pilulito.data.repositories.AuthRepository
import fr.utbm.sy43.pilulito.data.repositories.MedicationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SupervisorLogEntry(
    val intakeId: String,
    val drugName: String,
    val dosage: String,
    val scheduledTime: Long,
    val status: IntakeStatus
)

data class SupervisorUiState(
    val isLoading: Boolean = true,
    val seniorName: String = "",
    val entries: List<SupervisorLogEntry> = emptyList(),
    val errorMessage: String? = null
)

class SupervisorViewModel(
    private val authRepository: AuthRepository,
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupervisorUiState())
    val uiState: StateFlow<SupervisorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val supervisor = authRepository.currentUser.value
            val seniorId   = supervisor?.linkedSeniorId

            if (seniorId == null) {
                _uiState.value = SupervisorUiState(
                    isLoading = false,
                    errorMessage = "No senior account linked."
                )
                return@launch
            }

            combine(
                medicationRepository.intakes,
                medicationRepository.drugs
            ) { intakes, drugs ->
                // NOTE : le repo actuel est mono-utilisateur (pas de filtre par userId
                // sur les intakes). On affiche donc tous les intakes connus —
                // à filtrer par seniorId quand le repo deviendra multi-utilisateur.
                val entries = intakes
                    .mapNotNull { intake ->
                        val drug = drugs.find { it.id == intake.drugId } ?: return@mapNotNull null
                        SupervisorLogEntry(
                            intakeId      = intake.id,
                            drugName      = drug.name,
                            dosage        = drug.dosage,
                            scheduledTime = intake.realIntakeTime,
                            status        = intake.status
                        )
                    }
                    .sortedByDescending { it.scheduledTime }

                SupervisorUiState(
                    isLoading  = false,
                    seniorName = "Linked senior", // à remplacer par un lookup réel du nom
                    entries    = entries
                )
            }.collect { _uiState.value = it }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}