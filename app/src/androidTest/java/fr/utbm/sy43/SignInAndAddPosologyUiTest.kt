package fr.utbm.sy43

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.utbm.sy43.pilulito.data.models.Drug
import fr.utbm.sy43.pilulito.ui.screens.AddPosologyScreen // ⚠️ adjust to actual path
import fr.utbm.sy43.pilulito.viewmodels.AddPosologyViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddPosologyUiTest {

    @get:Rule
    val composeTestRule = createComposeRule() // ✅ no MainActivity, no auth, no real nav graph

    @Test
    fun savePrescription_fillsFormAndSaves() {
        val fakeRepo = FakeMedicationRepository()
        fakeRepo.setDrugs(
            listOf(Drug(id = "drug_1", name = "Doliprane", dosage = "1000mg", form = "Tablet"))
        )

        composeTestRule.setContent {
            val navController = rememberNavController() // ✅ a real NavController, just not driven by MainActivity
            val testViewModel = AddPosologyViewModel(
                application = androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application,
                medicationRepository = fakeRepo // ✅ your existing fake, no real DB
            )

            AddPosologyScreen(
                navController = navController,
                viewModel = testViewModel // ✅ bypasses AppViewModelProvider.Factory entirely
            )
        }

        composeTestRule.onNodeWithText("Select a medication").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Doliprane").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save prescription").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Doliprane", substring = true)
            .onFirst()
            .assertIsDisplayed()
    }
}