package com.example.senpos

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.senpos.data.AppContainer
import com.example.senpos.data.DefaultAppContainer
import com.example.senpos.notifications.NotificationHelper
import com.example.senpos.workers.OverdueCheckWorker
import com.google.android.libraries.places.api.Places
import java.util.concurrent.TimeUnit

class PillulitoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)

        val overdueCheck = PeriodicWorkRequestBuilder<OverdueCheckWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "overdue_check",
            ExistingPeriodicWorkPolicy.KEEP,   // ne recrée pas si déjà en cours
            overdueCheck
        )
        // Initialisation de Google Places avec la nouvelle API
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey != "PASTE_YOUR_API_KEY_HERE" && !Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
        
        container = DefaultAppContainer(this)
    }
}
