package com.canolabs.zonablava.ui.vehicles.addVehicle

import com.canolabs.zonablava.ui.common.UIState
import kotlinx.coroutines.flow.MutableStateFlow

data class AddVehicleUIState(
    val licensePlate: String = "",
    val brand: String = "",
    val model: String = "",
    val alias: String = "",
    val type: String = "",
    val licensePlateErrorId: Int? = null,
    override val isLoading: Boolean = false,
    override val loadingMessageId: Int? = null,
) : UIState

fun MutableStateFlow<AddVehicleUIState>.setLicensePlate(licensePlate: String) {
    value = value.copy(licensePlate = licensePlate)
}

fun MutableStateFlow<AddVehicleUIState>.setBrand(brand: String) {
    value = value.copy(brand = brand)
}

fun MutableStateFlow<AddVehicleUIState>.setModel(model: String) {
    value = value.copy(model = model)
}

fun MutableStateFlow<AddVehicleUIState>.setAlias(alias: String) {
    value = value.copy(alias = alias)
}

fun MutableStateFlow<AddVehicleUIState>.setType(type: String) {
    value = value.copy(type = type)
}

fun MutableStateFlow<AddVehicleUIState>.setLicensePlateError(licensePlateErrorId: Int?) {
    value = value.copy(licensePlateErrorId = licensePlateErrorId)
}