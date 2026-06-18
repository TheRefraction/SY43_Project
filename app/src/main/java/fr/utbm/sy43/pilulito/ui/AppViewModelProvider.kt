package fr.utbm.sy43.pilulito.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.utbm.sy43.pilulito.PillulitoApplication
import fr.utbm.sy43.pilulito.viewmodels.AccountInfoViewModel
import fr.utbm.sy43.pilulito.viewmodels.AddPosologyViewModel
import fr.utbm.sy43.pilulito.viewmodels.AuthViewModel
import fr.utbm.sy43.pilulito.viewmodels.HistoryViewModel
import fr.utbm.sy43.pilulito.viewmodels.HomeViewModel
import fr.utbm.sy43.pilulito.viewmodels.PharmacyViewModel
import fr.utbm.sy43.pilulito.viewmodels.SupervisorViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            AccountInfoViewModel(pillulitoApplication().container.authRepository)
        }

        initializer {
            PharmacyViewModel(pillulitoApplication().container.pharmacyRepository)
        }
        initializer {
            AuthViewModel(pillulitoApplication().container.authRepository)
        }
        initializer {
            HomeViewModel(pillulitoApplication().container.medicationRepository)
        }

        initializer {
            HistoryViewModel(pillulitoApplication().container.medicationRepository)
        }

        initializer {
            SupervisorViewModel(
                pillulitoApplication().container.authRepository,
                pillulitoApplication().container.medicationRepository
            )
        }

        initializer {
            val application =
                this[AndroidViewModelFactory.APPLICATION_KEY]
                        as PillulitoApplication

            AddPosologyViewModel(
                application,
                application.container.medicationRepository
            )
        }
    }
}

fun CreationExtras.pillulitoApplication(): PillulitoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PillulitoApplication)
