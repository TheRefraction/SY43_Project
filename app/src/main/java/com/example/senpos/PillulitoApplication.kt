package com.example.senpos

import android.app.Application
import com.example.senpos.data.AppContainer
import com.example.senpos.data.DefaultAppContainer
import com.google.android.libraries.places.api.Places

class PillulitoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        
        // Initialisation de Google Places avec la nouvelle API
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey != "PASTE_YOUR_API_KEY_HERE" && !Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
        
        container = DefaultAppContainer(this)
    }
}
