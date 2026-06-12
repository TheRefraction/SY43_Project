package com.example.senpos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.senpos.PillulitoApplication
import com.example.senpos.data.models.IntakeStatus
import com.example.senpos.notifications.NotificationHelper
import java.util.Calendar

class OverdueCheckWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val repo = (applicationContext as PillulitoApplication)
            .container.medicationRepository

        val now        = System.currentTimeMillis()
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val overdueCount = repo.intakes.value
            .count { it.realIntakeTime < startOfDay && it.status == IntakeStatus.PENDING }

        if (overdueCount > 0) {
            NotificationHelper.sendOverdueSummary(applicationContext, overdueCount)
        }

        return Result.success()
    }
}