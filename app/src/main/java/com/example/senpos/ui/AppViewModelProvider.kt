package com.example.senpos.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.senpos.PillulitoApplication
import com.example.senpos.viewmodels.AuthViewModel
import com.example.senpos.viewmodels.HomeViewModel
import com.example.senpos.viewmodels.PharmacyViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            PharmacyViewModel(pillulitoApplication().container.pharmacyRepository)
        }
        initializer {
            AuthViewModel(pillulitoApplication().container.authRepository)
        }
        initializer {
            HomeViewModel(pillulitoApplication().container.medicationRepository)
        }
    }
}

fun CreationExtras.pillulitoApplication(): PillulitoApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PillulitoApplication)
