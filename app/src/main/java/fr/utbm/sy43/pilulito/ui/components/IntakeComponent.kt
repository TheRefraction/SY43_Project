package fr.utbm.sy43.pilulito.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.utbm.sy43.pilulito.data.models.IntakeStatus
import fr.utbm.sy43.pilulito.viewmodels.IntakeCardUiModel
import fr.utbm.sy43.pilulito.viewmodels.OverdueIntakeUiModel
import fr.utbm.sy43.pilulito.ui.theme.SenPosTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IntakeComponent(
    intake: IntakeCardUiModel,
    onTake: (String) -> Unit
) {
    val alreadyActioned = intake.status != IntakeStatus.PENDING

    IntakeCard {
        IntakeInfo(
            drugName = intake.drugName,
            dosage   = intake.dosage,
            quantity = intake.quantity,
            time     = formatTime(intake.scheduledTime)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick  = { onTake(intake.intakeId) },
                enabled  = !intake.isUpcoming && !alreadyActioned,
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color.White,
                    contentColor           = Color(0xFFFF6C00),
                    disabledContainerColor = Color.White.copy(alpha = 0.4f),
                    disabledContentColor   = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = when {
                        intake.isUpcoming    -> "Upcoming"
                        alreadyActioned      -> if (intake.status == IntakeStatus.TAKEN) "Taken ✓" else "Missed"
                        else                 -> "I took it"
                    }
                )
            }
        }
    }
}


@Composable
fun OverdueIntakeComponent(
    intake: OverdueIntakeUiModel,
    onTake: (String) -> Unit,
    onMiss: (String) -> Unit
) {
    IntakeCard {
        IntakeInfo(
            drugName = intake.drugName,
            dosage   = intake.dosage,
            quantity = intake.quantity,
            time     = formatTime(intake.scheduledTime)
        )
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onTake(intake.intakeId) },
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor   = Color(0xFFFF6C00)
                )
            ) {
                Text("I took it")
            }
            Button(
                onClick = { onMiss(intake.intakeId) },
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.4f),
                    contentColor   = Color.White
                )
            ) {
                Text("I missed it")
            }
        }
    }
}

@Composable
private fun IntakeCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color    = Color(0xFFFF6C00),
        shape    = RoundedCornerShape(50.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            content()
        }
    }
}
@Composable
private fun IntakeInfo(drugName: String, dosage: String, quantity: Int, time: String) {
    // Ligne 1 : nom + dosage
    Text(
        text     = "$drugName $dosage",
        color    = Color.White,
        fontSize = 22.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp)
    )
    // Ligne 2 : quantité + heure
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text      = "$quantity pill${if (quantity > 1) "s" else ""}",
            color     = Color.White,
            fontSize  = 16.sp,
            fontStyle = FontStyle.Italic
        )
        Text(
            text     = time,
            color    = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))


@Preview
@Composable
fun IntakeTodayPreview() {
    SenPosTheme {
        IntakeComponent(
            intake = IntakeCardUiModel(
                intakeId      = "preview_1",
                drugName      = "Doliprane",
                dosage        = "1000mg",
                form          = "Tablet",
                quantity      = 1,
                scheduledTime = System.currentTimeMillis() - 3_600_000, // 1h ago
                status        = IntakeStatus.PENDING,
                isUpcoming    = false
            ),
            onTake = {}
        )
    }
}

@Preview
@Composable
fun IntakeUpcomingPreview() {
    SenPosTheme {
        IntakeComponent(
            intake = IntakeCardUiModel(
                intakeId      = "preview_2",
                drugName      = "Metformine",
                dosage        = "500mg",
                form          = "Tablet",
                quantity      = 1,
                scheduledTime = System.currentTimeMillis() + 3_600_000, // in 1h
                status        = IntakeStatus.PENDING,
                isUpcoming    = true
            ),
            onTake = {}
        )
    }
}

@Preview
@Composable
fun OverdueIntakePreview() {
    SenPosTheme {
        OverdueIntakeComponent(
            intake = OverdueIntakeUiModel(
                intakeId      = "preview_3",
                drugName      = "Amlodipine",
                dosage        = "5mg",
                form          = "Tablet",
                quantity      = 2,
                scheduledTime = System.currentTimeMillis() - 86_400_000 // yesterday
            ),
            onTake = {},
            onMiss = {}
        )
    }
}