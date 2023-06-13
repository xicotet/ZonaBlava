package com.canolabs.zonablava.data.source.remote

import android.util.Log
import com.canolabs.zonablava.data.source.model.GeocodingResponse
import com.canolabs.zonablava.data.source.model.GeocodingResult
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingApiService = retrofit.create(GeocodingApiService::class.java)

    suspend fun reverseGeocode(latlng: String, apiKey: String): String {
        try {
            val response: Response<GeocodingResponse> = geocodingApiService.reverseGeocode(latlng, apiKey)
            if (response.isSuccessful) {
                val geocodingResult: GeocodingResult? = response.body()?.results?.firstOrNull()
                val formattedAddress = geocodingResult?.formatted_address
                return formattedAddress ?: ""
            } else {
                Log.e("Geocoding", "Reverse geocoding request failed with error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Reverse geocoding request failed with exception: ${e.message}")
        }
        return ""
    }
}