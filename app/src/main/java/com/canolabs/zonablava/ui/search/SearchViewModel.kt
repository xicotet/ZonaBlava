package com.canolabs.zonablava.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canolabs.zonablava.data.source.model.DefaultPlaces
import com.canolabs.zonablava.data.source.model.Place
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val placesClient: PlacesClient
) : ViewModel() {

    private var searchJob: Job? = null // Keep track of the search coroutine job

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userSelection = MutableStateFlow(DefaultPlaces.VALENCIA)
    val userSelection: StateFlow<Place> = _userSelection

    private val _searchResults = MutableLiveData<List<Place>>()
    val searchResults = _searchResults

    init {

        // Throttle the search query flow
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Delay before emitting the latest search query
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        searchJob?.cancel() // Cancel the previous search coroutine job, if any
                        performPlaceAutocomplete(query)
                    }
                }
        }

    }

    fun setUserSelection(place: Place) {
        _userSelection.value = place
        Log.d("PassResults", "Search ViewModel updates the user selection flow: ${place.placeId}")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun performPlaceAutocomplete(query: String) {
        val bounds = LatLngBounds.builder()
            .include(LatLng(36.117577, -9.415672)) // Northwest corner of Spain
            .include(LatLng(43.848338, -9.393228)) // Northeast corner of Spain
            .include(LatLng(36.027655, -6.222583)) // Southwest corner of Spain
            .include(LatLng(43.739067, 3.407013))  // Southeast corner of Spain
            .build()

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setLocationBias(RectangularBounds.newInstance(bounds))
            .build()

        placesClient.findAutocompletePredictions(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val predictions = task.result?.autocompletePredictions.orEmpty()
                val suggestionItems = predictions.map { prediction ->
                    Place(
                        prediction.placeId,
                        prediction.getPrimaryText(null).toString(),
                        prediction.getSecondaryText(null).toString(),
                        null)
                }
                _searchResults.postValue(suggestionItems)
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    // Handle API exception
                }
            }
        }
    }
}