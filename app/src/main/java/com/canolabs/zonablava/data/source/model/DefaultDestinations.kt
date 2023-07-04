package com.canolabs.zonablava.data.source.model

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions

object DefaultDestinations {
    val BENIDORM = Destination(
        "defaultCityBenidorm",
        "Benidorm",
        "Provincia de Alicante",
        LatLng(38.538743, -0.128376)
    )

    val VALENCIA = Destination(
        "defaultCityValencia",
        "Valencia",
        "Provincia de Valencia",
        LatLng(39.467547, -0.367676)
    )

    val BETXI = Destination(
        "defaultCityBetxi",
        "Betxí",
        "Provincia de Castellón",
        LatLng(39.928454, -0.1998995)
    )
    val LAVALL = Destination(
        "defaultCityLaVall",
        "La Vall d'Uixó",
        "Provincia de Castellón",
        LatLng(39.8209435, -0.2269505)
    )
}

object DefaultParkingZonesLaVall {
    private val blueZonefillColor: Int = Color.argb(64, 205, 229, 255)
    val blueZoneBenigaslo = PolygonOptions()
        .add(
            LatLng(39.821944, -0.219762),
            LatLng(39.819265, -0.220036),
            LatLng(39.820178, -0.229779),
            LatLng(39.822551, -0.229519),
            LatLng(39.821944, -0.219762)
        )
        .fillColor(blueZonefillColor)
        .strokeColor(Color.BLUE)
        .strokeWidth(3.0f)

    private val orangeZoneFillColor: Int = Color.argb(64, 255, 179, 0)
    val orangeZoneCarbonaire = PolygonOptions()
        .add(
            LatLng(39.827778, -0.235524),
            LatLng(39.828808, -0.230632),
            LatLng(39.831774, -0.231855),
            LatLng(39.830538, -0.236511),
            LatLng(39.827778, -0.235524)
        )
        .fillColor(orangeZoneFillColor)
        .strokeColor(Color.GRAY)
        .strokeWidth(4.0f)
}