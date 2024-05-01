package com.canolabs.zonablava.ui.vehicles.addVehicle

import androidx.lifecycle.ViewModel
import com.canolabs.zonablava.R
import com.canolabs.zonablava.helpers.Constants
import com.canolabs.zonablava.helpers.Constants.MAX_VEHICLE_BRAND_LENGTH
import com.canolabs.zonablava.helpers.Constants.MAX_VEHICLE_MODEL_LENGTH
import com.canolabs.zonablava.helpers.Constants.VEHICLE_LICENSE_PLATE_PATTERN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddVehicleViewModel : ViewModel() {
    private var _state = MutableStateFlow(AddVehicleUIState())
    val state: StateFlow<AddVehicleUIState> = _state.asStateFlow()

    init {

    }
    fun onEvent(event: AddVehicleUIEvent) {
        when (event) {
            is AddVehicleUIEvent.ValidateLicensePlate -> validateLicensePlate()
        }
    }

    private fun validateLicensePlate() {
        val licensePlate = _state.value.licensePlate
        val licensePlatePattern = VEHICLE_LICENSE_PLATE_PATTERN.toRegex()

        if (!licensePlatePattern.matches(licensePlate)) {
            _state.setLicensePlateError(R.string.vehicles_license_plate_error)
        } else {
            _state.setLicensePlateError(null)
        }
    }

    fun onLicensePlateChanged(licensePlate: String) {
        _state.setLicensePlate(licensePlate.trim())
    }

    fun onBrandChanged(brand: String) {
        // Check the length of the brand
        if (brand.length < MAX_VEHICLE_BRAND_LENGTH) {
            _state.setBrand(brand)
        }
    }

    fun onModelChanged(model: String) {
        // Check the length of the model
        if (model.length < MAX_VEHICLE_MODEL_LENGTH) {
            _state.setModel(model)
        }
    }

    fun onAliasChanged(alias: String) {
        // Check the length of the alias
        if (alias.length < Constants.MAX_VEHICLE_ALIAS_LENGTH) {
            _state.setAlias(alias)
        }
    }
}