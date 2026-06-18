package fr.utbm.sy43.pilulito.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.utbm.sy43.pilulito.PilulitoApplication
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
            AccountInfoViewModel(pilulitoApplication().container.authRepository)
        }

        initializer {
            PharmacyViewModel(pilulitoApplication().container.pharmacyRepository)
        }
        initializer {
            AuthViewModel(pilulitoApplication().container.authRepository)
        }
        initializer {
            HomeViewModel(pilulitoApplication().container.medicationRepository)
        }

        initializer {
            HistoryViewModel(pilulitoApplication().container.medicationRepository)
        }

        initializer {
            SupervisorViewModel(
                pilulitoApplication().container.authRepository,
                pilulitoApplication().container.medicationRepository
            )
        }

        initializer {
            val application =
                this[AndroidViewModelFactory.APPLICATION_KEY]
                        as PilulitoApplication

            AddPosologyViewModel(
                application,
                application.container.medicationRepository
            )
        }
    }
}

fun CreationExtras.pilulitoApplication(): PilulitoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PilulitoApplication)
