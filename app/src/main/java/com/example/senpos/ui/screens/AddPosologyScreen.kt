package com.example.senpos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.senpos.data.models.Drug
import com.example.senpos.ui.AppViewModelProvider
import com.example.senpos.ui.components.NavBarComponent
import com.example.senpos.ui.components.TopBarComponent
import com.example.senpos.viewmodels.AddPosologyViewModel

private val Orange = Color(0xFFFF6C00)
private val OrangeLight = Color(0xFFFFF3EC)
private val CardBackground = Color(0xFFFAFAFA)

@Composable
fun AddPosologyScreen(
    navController: NavHostController,
    viewModel: AddPosologyViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Retour auto à Today quand la sauvegarde réussit
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarComponent(navController)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "New prescription",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Orange
            )

            SectionCard(title = "Medication") {
                DrugDropdown(
                    drugs          = uiState.drugs,
                    selectedDrug   = uiState.selectedDrug,
                    onDrugSelected = viewModel::onDrugSelected
                )
                uiState.selectedDrug?.let { drug ->
                    Spacer(modifier = Modifier.height(8.dp))
                    DrugInfoChips(drug)
                }
            }

            SectionCard(title = "Frequency") {
                FrequencySelector(
                    selected  = uiState.frequencyHours,
                    onSelect  = viewModel::onFrequencyChanged
                )
            }

            SectionCard(title = "First intake time") {
                TimeSelector(
                    hour       = uiState.startHour,
                    minute     = uiState.startMinute,
                    onHourChanged   = viewModel::onStartHourChanged,
                    onMinuteChanged = viewModel::onStartMinuteChanged
                )
            }


            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SectionCard(title = "Quantity") {
                        StepperField(
                            value     = uiState.quantity,
                            onIncrease = { viewModel.onQuantityChanged(uiState.quantity + 1) },
                            onDecrease = { viewModel.onQuantityChanged(uiState.quantity - 1) },
                            unit      = "unit(s)"
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    SectionCard(title = "Duration") {
                        StepperField(
                            value     = uiState.durationDays,
                            onIncrease = { viewModel.onDurationChanged(uiState.durationDays + 1) },
                            onDecrease = { viewModel.onDurationChanged(uiState.durationDays - 1) },
                            unit      = "day(s)"
                        )
                    }
                }
            }

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }


            Button(
                onClick  = viewModel::savePosology,
                enabled  = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        NavBarComponent(navController)
    }
}


@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF888888))
        content()
    }
}

@Composable
private fun DrugDropdown(
    drugs: List<Drug>,
    selectedDrug: Drug?,
    onDrugSelected: (Drug) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OrangeLight)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text      = selectedDrug?.let { "${it.name} — ${it.dosage}" } ?: "Select a medication",
                fontSize  = 15.sp,
                color     = if (selectedDrug != null) Color(0xFF222222) else Color(0xFF999999),
                fontWeight = if (selectedDrug != null) FontWeight.Medium else FontWeight.Normal
            )
            Icon(
                imageVector        = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint               = Orange
            )
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White)
        ) {
            drugs.forEach { drug ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(drug.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text(
                                "${drug.dosage} · ${drug.form}",
                                fontSize = 12.sp,
                                color    = Color.Gray
                            )
                        }
                    },
                    onClick = {
                        onDrugSelected(drug)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DrugInfoChips(drug: Drug) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoChip(drug.dosage)
        InfoChip(drug.form)
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(OrangeLight)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, color = Orange, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FrequencySelector(selected: Int, onSelect: (Int) -> Unit) {
    val options = listOf(8 to "Every 8h", 12 to "Every 12h", 24 to "Every 24h")
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (hours, label) ->
            val isSelected = selected == hours
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Orange else OrangeLight)
                    .clickable { onSelect(hours) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = label,
                    color      = if (isSelected) Color.White else Orange,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TimeSelector(
    hour: Int,
    minute: Int,
    onHourChanged: (Int) -> Unit,
    onMinuteChanged: (Int) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Heures
        OutlinedTextField(
            value         = hour.toString().padStart(2, '0'),
            onValueChange = { v -> v.toIntOrNull()?.takeIf { it in 0..23 }?.let(onHourChanged) },
            label         = { Text("Hour") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.weight(1f),
            singleLine    = true,
            colors        = outlinedTextFieldColors()
        )
        Text(":", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Orange)
        // Minutes
        OutlinedTextField(
            value         = minute.toString().padStart(2, '0'),
            onValueChange = { v -> v.toIntOrNull()?.takeIf { it in 0..59 }?.let(onMinuteChanged) },
            label         = { Text("Minute") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier      = Modifier.weight(1f),
            singleLine    = true,
            colors        = outlinedTextFieldColors()
        )
    }
}

@Composable
private fun StepperField(
    value: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    unit: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier              = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick  = onDecrease,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OrangeLight)
        ) {
            Text("−", fontSize = 20.sp, color = Orange, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = value.toString(),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF222222)
            )
            Text(unit, fontSize = 11.sp, color = Color.Gray)
        }
        IconButton(
            onClick  = onIncrease,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OrangeLight)
        ) {
            Text("+", fontSize = 20.sp, color = Orange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Orange,
    unfocusedBorderColor = Color(0xFFDDDDDD),
    focusedLabelColor    = Orange
)