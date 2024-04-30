package com.canolabs.zonablava.ui.vehicles

import androidx.lifecycle.ViewModel
import com.canolabs.zonablava.data.source.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehiclesViewModel : ViewModel() {

    private var _state = MutableStateFlow(VehiclesUIState())
    val state: StateFlow<VehiclesUIState> = _state.asStateFlow()

    private val vehicle1 = Vehicle("3088BJL", "Nissan", "Almera Tino", "DailyCar", "Blue")
    private val vehicles = listOf(vehicle1)

    init {
        _state.setVehicles(vehicles)
    }

}