package com.canolabs.zonablava.ui.vehicles

import com.canolabs.zonablava.data.source.model.Vehicle
import com.canolabs.zonablava.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow

data class VehiclesUIState(
    val vehicles: List<Vehicle> = emptyList(),
    override val isLoading: Boolean = false,
    override val loadingMessageId: Int? = null,
) : UIState

fun MutableStateFlow<VehiclesUIState>.setIsLoading(isLoading: Boolean) {
    value = value.copy(isLoading = isLoading)
}

fun MutableStateFlow<VehiclesUIState>.setVehicles(vehicles: List<Vehicle>) {
    value = value.copy(vehicles = vehicles)
}