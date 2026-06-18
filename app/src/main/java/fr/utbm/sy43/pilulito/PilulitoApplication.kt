package fr.utbm.sy43.pilulito

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fr.utbm.sy43.pilulito.data.AppContainer
import fr.utbm.sy43.pilulito.data.DefaultAppContainer
import fr.utbm.sy43.pilulito.notifications.NotificationHelper
import fr.utbm.sy43.pilulito.workers.OverdueCheckWorker
import com.google.android.libraries.places.api.Places
import java.util.concurrent.TimeUnit

class PilulitoApplication : Application() {
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
