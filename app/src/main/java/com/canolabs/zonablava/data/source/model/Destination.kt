package com.canolabs.zonablava.data.source.model

import com.google.android.gms.maps.model.LatLng

data class Destination(
    val placeId: String,
    val name: String,
    val description: String,
    val location: LatLng?
)