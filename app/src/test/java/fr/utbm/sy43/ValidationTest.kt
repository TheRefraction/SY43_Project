package fr.utbm.sy43

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import fr.utbm.sy43.pilulito.data.models.Drug
import fr.utbm.sy43.pilulito.data.repositories.MedicationRepository
import fr.utbm.sy43.pilulito.viewmodels.AddPosologyViewModel
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ValidationTest {

    private fun createViewModel(
        repo: MedicationRepository = FakeMedicationRepository(),
        application: Application = ApplicationProvider.getApplicationContext()
    ): AddPosologyViewModel {
        return AddPosologyViewModel(application, repo)
    }

    @Test
    fun onQuantityChanged_updatesQuantity_whenValid() {
        val viewModel = createViewModel()
        viewModel.onQuantityChanged(3)
        assertEquals(3, viewModel.uiState.value.quantity)
    }

    @Test
    fun onQuantityChanged_ignoresInvalidValue() {
        val viewModel = createViewModel()
        viewModel.onQuantityChanged(0) // guard: qty >= 1
        assertEquals(1, viewModel.uiState.value.quantity) // unchanged from default
    }

    @Test
    fun onDurationChanged_ignoresZeroOrNegative() {
        val viewModel = createViewModel()
        viewModel.onDurationChanged(-5)
        assertEquals(7, viewModel.uiState.value.durationDays) // unchanged from default
    }

    @Test
    fun onDrugSelected_updatesSelectedDrugAndClearsError() {
        val viewModel = createViewModel()
        val drug = Drug(id = "drug_1", name = "Doliprane", dosage = "1000mg", form = "Tablet")
        viewModel.onDrugSelected(drug)
        assertEquals(drug, viewModel.uiState.value.selectedDrug)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}