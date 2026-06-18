package fr.utbm.sy43.pilulito.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import fr.utbm.sy43.pilulito.MainActivity
import fr.utbm.sy43.pilulito.R

object NotificationHelper {

    private const val CHANNEL_INTAKE   = "channel_intake"
    private const val CHANNEL_OVERDUE  = "channel_overdue"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)

            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_INTAKE,
                    "Medication reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Reminder at each scheduled intake time" }
            )

            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_OVERDUE,
                    "Missed medications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Alert for pending past intakes" }
            )
        }
    }

    fun sendIntakeReminder(context: Context, drugName: String, dosage: String, notifId: Int) {
        val intent = PendingIntent.getActivity(
            context, notifId,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, CHANNEL_INTAKE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time to take your medication")
            .setContentText("$drugName — $dosage")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(notifId, notif)
    }

    fun sendOverdueSummary(context: Context, count: Int) {
        val intent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, CHANNEL_OVERDUE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Missed medications")
            .setContentText("You have $count medication(s) not yet confirmed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(9999, notif)
    }
}