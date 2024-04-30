package com.canolabs.zonablava.ui.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canolabs.zonablava.R
import com.canolabs.zonablava.data.source.model.Vehicle
import com.canolabs.zonablava.ui.ezraFamily
import com.canolabs.zonablava.ui.robotoFamily

@Composable
fun VehicleCard(vehicle: Vehicle) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors =  CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.md_theme_light_secondary),
            contentColor = colorResource(id = R.color.md_theme_light_onSecondary),
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = vehicle.licencePlate,
                fontSize = 24.sp,
                fontFamily = robotoFamily,
                letterSpacing = 2.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.car_normal), // replace with your car icon resource id
                    contentDescription = "Car Icon",
                    modifier = Modifier.size(64.dp),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.md_theme_light_onPrimary)) // replace with a function that maps vehicle.color to a Color object
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, top = 8.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = vehicle.carBrand + " " + vehicle.carModel,
                        fontSize = 18.sp,
                        fontFamily = robotoFamily
                    )
                    Text(
                        text = vehicle.alias,
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 18.sp,
                        fontFamily = ezraFamily
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Arrow Icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}