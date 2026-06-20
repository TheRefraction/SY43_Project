package fr.utbm.sy43.pilulito.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.utbm.sy43.pilulito.data.repositories.PharmacyRepository
import fr.utbm.sy43.pilulito.data.models.Pharmacy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PharmacyUiState(
    val pharmacies: List<Pharmacy> = emptyList(),
    val isLoading: Boolean = false,
    val userLocation: Pair<Double, Double>? = null
)

class PharmacyViewModel(private val pharmacyRepository: PharmacyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PharmacyUiState())
    val uiState: StateFlow<PharmacyUiState> = _uiState.asStateFlow()

    fun loadPharmacies(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, userLocation = Pair(latitude, longitude))
            val result = pharmacyRepository.getNearbyPharmacies(latitude, longitude)
            _uiState.value = _uiState.value.copy(pharmacies = result, isLoading = false)
        }
    }
}
