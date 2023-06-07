package com.canolabs.zonablava.ui.home

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import com.canolabs.zonablava.helpers.Constants
import com.google.android.gms.location.Priority
import com.canolabs.zonablava.helpers.extensions.resumeWithLatLng

class LocationFetcher(private val context: Context) {

    private val defaultLocation =
        LatLng(Constants.DEFAULT_LOCATION_LATITUDE, Constants.DEFAULT_LOCATION_LATITUDE)
    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun fetchLocation(): LatLng {
        return suspendCancellableCoroutine { continuation ->
            try {
                if (isLocationEnabled()) {
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY, null
                    ).addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            continuation.resumeWithLatLng(latLng)
                        } else {
                            // 1. Location is turned off in the device settings and clears the cache.
                            // 2. The device never recorded its location, which could be the case of a new device
                            // 3. Google Play services on the device has restarted, and there is no active Fused Location Provider client

                            // Show system location enabling dialog since it's not available
                            // If last known location is null, show a default point in the map

                            // Could create a default exception and pass it right here with
                            // resumeWithException instead of resumeWithLatLng
                            Log.w("permission_granted",
                                "LOCATION FETCHER: Location is turned off in the device settings or the device never recorded its location")
                            continuation.resumeWithLatLng(defaultLocation)
                        }
                    }.addOnFailureListener {exception ->
                        // Handle exception if location retrieval fails
                        continuation.resumeWithException(exception)
                    }
                } else {
                    Log.w("permission_granted", "LOCATION FETCHER: Not GPS nor network abailable")
                    // Not GPS nor Network  available. Show system dialog or bottom sheet informing the situation
                    // The map view itself is not showing because neither Room nor Jetpack Datastore are implemented yet
                    continuation.resumeWithLatLng(defaultLocation)
                }
            } catch (s: SecurityException) {
                // Shouldn't arrive here because permission should be available before entering this fetchLocation()
                // fusedLocationProviderClient requires permission which may be rejected by user
                Log.e(
                    "permission_granted",
                    "LOCATION FETCHER: SecurityException, shouldn't arrive here since we managed permissions to be allowed before entering here"
                )
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}