package com.example.senpos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.senpos.ui.AppViewModelProvider
import com.example.senpos.ui.components.NavBarComponent
import com.example.senpos.ui.components.TopBarComponent
import com.example.senpos.viewmodels.DayStats
import com.example.senpos.viewmodels.DrugStat
import com.example.senpos.viewmodels.HistoryViewModel

private val Orange      = Color(0xFFFF6C00)
private val OrangeLight = Color(0xFFFFF3EC)
private val Green       = Color(0xFF4CAF50)
private val Red         = Color(0xFFE53935)
private val Grey        = Color(0xFFBDBDBD)
private val CardBg      = Color(0xFFFAFAFA)

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarComponent(navController)

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Orange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding    = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                item {
                    Text(
                        text       = "My history",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Orange
                    )
                }


                item {
                    GlobalScoreCard(
                        taken = uiState.globalTaken,
                        total = uiState.globalTotal
                    )
                }


                item { Legend() }

                item {
                    SectionLabel("Last 7 days")
                    Spacer(Modifier.height(8.dp))
                    WeekBarChart(days = uiState.weekStats)
                }

                item { SectionLabel("By medication") }

                items(uiState.drugStats, key = { it.drugName }) { stat ->
                    DrugStatRow(stat)
                }

                if (uiState.drugStats.isEmpty()) {
                    item {
                        Text(
                            text     = "No data for the last 7 days.",
                            color    = Color.Gray,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }

        NavBarComponent(navController)
    }
}


@Composable
private fun GlobalScoreCard(taken: Int, total: Int) {
    val ratio = if (total == 0) 0f else taken.toFloat() / total

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = OrangeLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text       = "$taken / $total",
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Orange
                )
                Text(
                    text     = "medications taken overall",
                    fontSize = 14.sp,
                    color    = Color(0xFF888888)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress         = { ratio },
                    modifier         = Modifier.size(72.dp),
                    strokeWidth      = 7.dp,
                    color            = Green,
                    trackColor       = Red.copy(alpha = 0.25f)
                )
                Text(
                    text       = "${(ratio * 100).toInt()}%",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
private fun Legend() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendDot(color = Green, label = "Taken")
        LegendDot(color = Red,   label = "Missed")
        LegendDot(color = Grey,  label = "Pending")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(label, fontSize = 13.sp, color = Color(0xFF666666))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        fontSize   = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color      = Color(0xFF333333)
    )
}

@Composable
private fun WeekBarChart(days: List<DayStats>) {
    val maxBarHeight = 100.dp

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom
        ) {
            days.forEach { day ->
                DayBar(day = day, maxBarHeight = maxBarHeight.value)
            }
        }
    }
}

@Composable
private fun DayBar(day: DayStats, maxBarHeight: Float) {
    val total = day.total.coerceAtLeast(1)

    val takenH   = (day.taken.toFloat()   / total * maxBarHeight).dp
    val missedH  = (day.missed.toFloat()  / total * maxBarHeight).dp
    val pendingH = (day.pending.toFloat() / total * maxBarHeight).dp
    val emptyH   = if (day.total == 0) maxBarHeight.dp else 0.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier            = Modifier.width(36.dp)
    ) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(maxBarHeight.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                if (day.total == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(emptyH)
                            .background(Color(0xFFEEEEEE))
                    )
                } else {
                    if (day.pending > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(pendingH)
                                .background(Grey)
                        )
                    }
                    if (day.missed > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(missedH)
                                .background(Red)
                        )
                    }
                    if (day.taken > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(takenH)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(Green)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text     = day.label,
            fontSize = 11.sp,
            color    = Color(0xFF888888)
        )
    }
}

@Composable
private fun DrugStatRow(stat: DrugStat) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = stat.drugName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = Color(0xFF222222)
                    )
                    Text(
                        text     = stat.dosage,
                        fontSize = 13.sp,
                        color    = Color.Gray
                    )
                }
                Text(
                    text       = "${stat.taken}/${stat.total}",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = if (stat.takenRatio >= 0.8f) Green else Orange
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
            ) {
                val total = stat.total.coerceAtLeast(1)
                val takenW   = stat.taken.toFloat()   / total
                val missedW  = stat.missed.toFloat()  / total
                val pendingW = stat.pending.toFloat()  / total

                if (stat.taken > 0)   Box(Modifier.weight(takenW).fillMaxHeight().background(Green))
                if (stat.missed > 0)  Box(Modifier.weight(missedW).fillMaxHeight().background(Red))
                if (stat.pending > 0) Box(Modifier.weight(pendingW).fillMaxHeight().background(Grey))

                if (stat.total == 0)  Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFEEEEEE)))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill(count = stat.taken,   color = Green, label = "taken")
                StatPill(count = stat.missed,  color = Red,   label = "missed")
                if (stat.pending > 0)
                    StatPill(count = stat.pending, color = Grey, label = "pending")
            }
        }
    }
}

@Composable
private fun StatPill(count: Int, color: Color, label: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text("$count $label", fontSize = 12.sp, color = Color(0xFF666666))
    }
}