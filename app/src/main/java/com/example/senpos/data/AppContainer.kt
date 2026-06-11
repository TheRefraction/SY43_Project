package com.example.senpos.data

import android.content.Context
import com.example.senpos.data.repositories.AuthRepository
import com.example.senpos.data.repositories.GooglePlacesPharmacyRepository
import com.example.senpos.data.repositories.MedicationRepository
import com.example.senpos.data.repositories.OfflineAuthRepository
import com.example.senpos.data.repositories.OfflineMedicationRepository
import com.example.senpos.data.repositories.PharmacyRepository

interface AppContainer {
    val pharmacyRepository: PharmacyRepository
    val authRepository: AuthRepository
    val medicationRepository: MedicationRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val pharmacyRepository: PharmacyRepository by lazy {
        GooglePlacesPharmacyRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        OfflineAuthRepository()
    }

    override val medicationRepository: MedicationRepository by lazy {
        OfflineMedicationRepository()
    }
}
