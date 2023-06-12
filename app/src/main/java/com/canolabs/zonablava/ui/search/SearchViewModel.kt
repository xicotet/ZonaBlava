package com.canolabs.zonablava.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canolabs.zonablava.data.source.model.DefaultDestinations
import com.canolabs.zonablava.data.source.model.Destination
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
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

    private val _userSelection = MutableStateFlow(DefaultDestinations.VALENCIA)
    val userSelection: StateFlow<Destination> = _userSelection

    val userSelectionUpdating = MutableStateFlow(false)

    private val _searchResults = MutableLiveData<List<Destination>>()
    val searchResults = _searchResults

    val defaultDestinations = arrayListOf<Destination>(
        DefaultDestinations.BETXI,
        DefaultDestinations.LAVALL,
        DefaultDestinations.VALENCIA,
        DefaultDestinations.BENIDORM
    )

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

    fun setUserSelection(destination: Destination) {
        userSelectionUpdating.value = true
        if (!defaultDestinations.contains(destination)){
            fetchLocationForSelectedSuggestion(destination)
        } else {
            _userSelection.value = destination
            userSelectionUpdating.value = false
            Log.d(
                "PassResults",
                "Search ViewModel updates the user selection flow: ${destination.placeId} : ${destination.location}"
            )
        }


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
            .setLocationRestriction(RectangularBounds.newInstance(bounds))
            .build()

        placesClient.findAutocompletePredictions(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val predictions = task.result?.autocompletePredictions.orEmpty()
                val suggestionItems = predictions.map { prediction ->
                    Destination(
                        prediction.placeId,
                        prediction.getPrimaryText(null).toString(),
                        prediction.getSecondaryText(null).toString(),
                        null
                    )
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

    private fun fetchLocationForSelectedSuggestion(selectedSuggestion: Destination) {
        val placeId = selectedSuggestion.placeId
        val placeFields = listOf(Place.Field.LAT_LNG)

        val placeRequest = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(placeRequest).addOnCompleteListener { detailsTask ->
            if (detailsTask.isSuccessful) {
                val place = detailsTask.result?.place
                if (place != null) {
                    val updatedSuggestion = selectedSuggestion.copy(location = place.latLng)
                    Log.d("PassResults", "fetchLocation fetched the latlng of place: ${place.latLng}")
                    _userSelection.value = updatedSuggestion
                    Log.d(
                        "PassResults",
                        "Search ViewModel updates the user selection flow: ${updatedSuggestion.placeId} : ${updatedSuggestion.location}"
                    )
                    userSelectionUpdating.value = false
                }
            } else {
                val exception = detailsTask.exception
                if (exception is ApiException) {
                    //val statusCode = exception.statusCode
                    //Handle error with given status code
                }
            }
        }
    }
}