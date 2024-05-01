package com.canolabs.zonablava.ui.vehicles.addVehicle

import androidx.lifecycle.ViewModel
import com.canolabs.zonablava.data.source.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddVehicleViewModel : ViewModel() {
    private var _state = MutableStateFlow(AddVehicleUIState())
    val state: StateFlow<AddVehicleUIState> = _state.asStateFlow()

    private val vehicle1 = Vehicle("3088BJL", "Nissan", "Almera Tino", "DailyCar", "Blue")
    private val vehicles = listOf(vehicle1)

    init {

    }
}