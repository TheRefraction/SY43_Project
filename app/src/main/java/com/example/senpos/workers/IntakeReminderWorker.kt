package com.example.senpos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.senpos.notifications.NotificationHelper


class IntakeReminderWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val drugName = inputData.getString("drug_name") ?: return Result.failure()
        val dosage   = inputData.getString("dosage")    ?: return Result.failure()
        val notifId  = inputData.getInt("notif_id", 0)

        NotificationHelper.sendIntakeReminder(applicationContext, drugName, dosage, notifId)
        return Result.success()
    }
}