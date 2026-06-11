package com.example.senpos.data.repositories

import android.content.Context
import com.example.senpos.data.models.Pharmacy
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.tasks.await

interface PharmacyRepository {
    suspend fun getNearbyPharmacies(latitude: Double, longitude: Double): List<Pharmacy>
}

class GooglePlacesPharmacyRepository(context: Context) : PharmacyRepository {
    private val placesClient = Places.createClient(context)

    override suspend fun getNearbyPharmacies(latitude: Double, longitude: Double): List<Pharmacy> {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION
        )
        
        val circle = CircularBounds.newInstance(LatLng(latitude, longitude), 5000.0)
        
        val searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
            .setIncludedTypes(listOf("pharmacy"))
            .setMaxResultCount(20)
            .build()

        return try {
            val task = placesClient.searchNearby(searchNearbyRequest)
            val response = task.await()
            response.places.map { place ->
                Pharmacy(
                    id = place.id ?: "",
                    name = place.displayName ?: "Pharmacie inconnue",
                    address = place.formattedAddress ?: "Adresse non disponible",
                    latitude = place.location?.latitude ?: latitude,
                    longitude = place.location?.longitude ?: longitude
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
