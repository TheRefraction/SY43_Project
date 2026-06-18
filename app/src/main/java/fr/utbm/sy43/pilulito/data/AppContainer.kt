package fr.utbm.sy43.pilulito.data

import android.content.Context
import fr.utbm.sy43.pilulito.data.repositories.AuthRepository
import fr.utbm.sy43.pilulito.data.repositories.GooglePlacesPharmacyRepository
import fr.utbm.sy43.pilulito.data.repositories.MedicationRepository
import fr.utbm.sy43.pilulito.data.repositories.OfflineAuthRepository
import fr.utbm.sy43.pilulito.data.repositories.OfflineMedicationRepository
import fr.utbm.sy43.pilulito.data.repositories.PharmacyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.utbm.sy43.pilulito.data.repositories.FirestoreAuthRepository
import fr.utbm.sy43.pilulito.data.repositories.FirestoreMedicationRepository

interface AppContainer {
    val pharmacyRepository: PharmacyRepository
    val authRepository: AuthRepository
    val medicationRepository: MedicationRepository

    val firestore : FirebaseFirestore

    val firebaseAuth: FirebaseAuth
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val firestore: FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }

    override val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override val pharmacyRepository: PharmacyRepository by lazy {
        GooglePlacesPharmacyRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        FirestoreAuthRepository(firestore)
    }

    override val medicationRepository: MedicationRepository by lazy {
        FirestoreMedicationRepository(firestore)
    }
}
