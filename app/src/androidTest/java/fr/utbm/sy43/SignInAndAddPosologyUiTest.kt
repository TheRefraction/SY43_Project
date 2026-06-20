package fr.utbm.sy43

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.utbm.sy43.pilulito.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpAndAddPosologyUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun savePrescription_fillsFormAndSaves() {

        composeTestRule.onNodeWithText("Don't have an account? Click here").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("Name").performTextInput("John")
        composeTestRule.onNodeWithTag("Surname").performTextInput("Doe")
        composeTestRule.onNodeWithTag("Login").performTextInput("johndoe@aaaaaa.com")
        composeTestRule.onNodeWithTag("Password").performTextInput("johndoe1234")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sign up").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().printToLog("UI_TREE")

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("Add med")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("Add med").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Select a medication").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Doliprane").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save prescription").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Doliprane").assertIsDisplayed()
    }
}