package com.example.senpos.ui

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.senpos.PillulitoApplication
import com.example.senpos.viewmodels.AccountInfoViewModel
import com.example.senpos.viewmodels.AddPosologyViewModel
import com.example.senpos.viewmodels.AuthViewModel
import com.example.senpos.viewmodels.HistoryViewModel
import com.example.senpos.viewmodels.HomeViewModel
import com.example.senpos.viewmodels.PharmacyViewModel
import com.example.senpos.viewmodels.SupervisorViewModel

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
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
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
