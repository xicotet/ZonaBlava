package com.canolabs.zonablava.data.source.model

data class GeocodingResponse(
    val results: List<GeocodingResult>
)

data class GeocodingResult(
    val formatted_address: String
)