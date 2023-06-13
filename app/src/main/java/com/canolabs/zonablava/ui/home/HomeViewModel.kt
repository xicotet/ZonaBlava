package com.canolabs.zonablava.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canolabs.zonablava.BuildConfig
import com.canolabs.zonablava.data.source.model.DefaultDestinations
import com.canolabs.zonablava.data.source.model.Destination
import com.canolabs.zonablava.data.source.remote.GeocodingApiClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val lastMarkerDestination = MutableStateFlow(DefaultDestinations.VALENCIA)
    private val lastUserSearchSelection = MutableStateFlow(DefaultDestinations.VALENCIA)

    private val geocodingApiClient = GeocodingApiClient()
    private val _formattedAddress = MutableStateFlow("")
    val formattedAddress: StateFlow<String> = _formattedAddress

    // Eventually, this flow should substitute the markerParkCar of HomeFragment
    private val markerPositionFlow = MutableStateFlow<LatLng?>(null)

    init {
        viewModelScope.launch {
            markerPositionFlow
                .debounce(500) // Delay before emitting the latest marker position
                .filterNotNull()
                .collect { markerPosition ->
                    performReverseGeocoding(markerPosition)
                }
        }
    }

    fun setLastMarkerDestination(destination: Destination) {
        Log.d("PassResults", "HomeViewModel. setLastMarker: ${destination.placeId} : ${destination.location}")
        lastMarkerDestination.value = destination
    }

    fun getLastMarkerDestination(): Destination {
        return lastMarkerDestination.value
    }

    fun setLastUserSearchSelection(destination: Destination) {
        lastUserSearchSelection.value = destination
    }

    fun getLastUserSearchDestination(): Destination {
        return lastUserSearchSelection.value
    }

    fun updateMarkerPosition(markerPosition: LatLng) {
        markerPositionFlow.value = markerPosition
    }

    private fun performReverseGeocoding(markerPosition: LatLng) {
        val latlng = "${markerPosition.latitude},${markerPosition.longitude}"
        val apiKey = BuildConfig.MAPS_API_KEY

        viewModelScope.launch {
            try {
                val result = geocodingApiClient.reverseGeocode(latlng, apiKey)
                _formattedAddress.value = result
                Log.d("Geocoding", "Formatted Address: $result")
            } catch (e: Exception) {
                Log.e("Geocoding", "Reverse geocoding request failed with exception: ${e.message}")
            }
        }
    }
}