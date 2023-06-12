package com.canolabs.zonablava.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.canolabs.zonablava.data.source.model.DefaultDestinations
import com.canolabs.zonablava.data.source.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val lastMarkerDestination = MutableStateFlow(DefaultDestinations.VALENCIA)
    private val lastUserSearchSelection = MutableStateFlow(DefaultDestinations.VALENCIA)

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
}