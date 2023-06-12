package com.canolabs.zonablava.data.source.model

import com.google.android.gms.maps.model.LatLng

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
        LatLng(39.4079343, -0.5263206)
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
        LatLng(39.8209435,-0.2269505)
    )
}