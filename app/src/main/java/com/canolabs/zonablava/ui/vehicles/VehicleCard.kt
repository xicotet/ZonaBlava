package com.canolabs.zonablava.ui.vehicles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.canolabs.zonablava.data.source.model.Vehicle

@Composable
fun VehicleCard(vehicle: Vehicle) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "License Plate: ${vehicle.licencePlate}")
            Text(text = "Brand: ${vehicle.carBrand}")
            Text(text = "Model: ${vehicle.carModel}")
            Text(text = "Alias: ${vehicle.alias}")
            Text(text = "Color: ${vehicle.color}")
        }
    }
}