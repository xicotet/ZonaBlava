package com.canolabs.zonablava.ui.vehicles.addVehicle

sealed class AddVehicleUIEvent {
    data object ValidateLicensePlate : AddVehicleUIEvent()
}