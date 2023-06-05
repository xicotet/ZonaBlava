package com.canolabs.zonablava.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.canolabs.zonablava.R
import com.canolabs.zonablava.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.canolabs.zonablava.databinding.BottomSheetLocationPermissionBinding
import com.canolabs.zonablava.databinding.BottomSheetChangeToFineLocationBinding
import android.provider.Settings
import androidx.annotation.RequiresApi


class HomeFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    private var bottomSheetPermissionDialog: BottomSheetDialog? = null
    private var isBottomSheetPermissionDialogShowing: Boolean = false

    private var bottomSheetChangeToFineLocationDialog: BottomSheetDialog? = null
    private var isBottomSheetChangeToFineLocationDialogShowing: Boolean = false

    private var systemRationaleAppearanceCount: Int = 0

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.N)
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            systemRationaleAppearanceCount++
            Log.d("permission_granted", "3. rationaleAppearanceCount: $systemRationaleAppearanceCount")
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.d("permission_granted", "3. Precise location access granted ")
                    Log.d("permission_granted", "3. Get user precise location asynchronously ")
                    binding.myLocationButton.setImageResource(R.drawable.my_location_fill)

                    if (isBottomSheetChangeToFineLocationDialogShowing) {
                        Log.d("permission_granted", "3. ChangeToFine location bottom sheet was showing, proceed to hide")
                        isBottomSheetChangeToFineLocationDialogShowing = false
                        bottomSheetChangeToFineLocationDialog?.dismiss()
                        bottomSheetChangeToFineLocationDialog = null

                        isBottomSheetPermissionDialogShowing = false
                        bottomSheetPermissionDialog?.dismiss()
                        bottomSheetPermissionDialog = null
                    }
                    if (isBottomSheetPermissionDialogShowing) {
                        Log.d("permission_granted", "3. Standard location bottom sheet was showing, proceed to hide")
                        isBottomSheetPermissionDialogShowing = false
                        bottomSheetPermissionDialog?.hide()
                    }

                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.d("permission_granted", "3. Approximate location access granted ")
                    Log.d("permission_granted", "3. Get user approximate location asynchronously ")
                    binding.myLocationButton.setImageResource(R.drawable.my_location_aproximate)
                    if (isBottomSheetPermissionDialogShowing) {
                        isBottomSheetPermissionDialogShowing = false
                        bottomSheetPermissionDialog?.hide()
                    }
                    showChangeToFineLocationBottomSheet()
                }

                else -> {
                    // No location access granted. This can be reached even if the user don't presses 'Disallow' button
                    Log.d("permission_granted", "3. No location access granted")
                    if (systemRationaleAppearanceCount > 2 || !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Log.d("permission_granted", "3. System rationale Appearence count is grater than 2 or the permission has been denied")
                        openAppSettings()
                    } else {
                        Log.d("permission_granted", "3. System rationale Appearence count is not grater than 2")
                        isBottomSheetPermissionDialogShowing = true // May not be necessary if always is true when arriving here?
                        bottomSheetPermissionDialog?.show()
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.myLocationButton -> {
                Log.d("permission_granted", "0. Button MyLocation clicked")
                if (arePermissionsGranted(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
                    Log.d("permission_granted", "0. Precise location access granted ")
                    Log.d("permission_granted", "0. Get user precise location asynchronously ")
                    binding.myLocationButton.setImageResource(R.drawable.my_location_fill)
                } else if (arePermissionsGranted(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    bottomSheetChangeToFineLocationDialog?.hide()
                    isBottomSheetChangeToFineLocationDialogShowing = false
                    Log.d("permission_granted", "0. Aproximate location access granted ")
                    Log.d("permission_granted", "0. Get user approximate location asynchronously")
                    binding.myLocationButton.setImageResource(R.drawable.my_location_aproximate)
                    showChangeToFineLocationBottomSheet()
                } else {
                    requestLocationPermissions()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showChangeToFineLocationBottomSheet(){
        if (bottomSheetChangeToFineLocationDialog == null) {
            systemRationaleAppearanceCount
            Log.d("permission_granted", "4. First time ChangeToFineLocation bottom sheet creation")
            val binding = BottomSheetChangeToFineLocationBinding.inflate(layoutInflater)
            bottomSheetChangeToFineLocationDialog = BottomSheetDialog(requireContext()) //TODO Check requireContext() is never null
            bottomSheetChangeToFineLocationDialog!!.setContentView(binding.root)

            binding.bottomSheetChangeToFineLocationAllowButton.setOnClickListener {
                Log.d("permission_granted", "4. Allow button clicked in ChangeToFineLocation Bottom Sheet Dialog")

                if (systemRationaleAppearanceCount < 3){
                    //You can arrive here even if the user presses out the system location permission dialog
                    Log.d("permission_granted", "4. Should open permission request rationale")
                    requestLocationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                } else {
                    Log.d("permission_granted", "4. Should not open permission request rationale. Open app configuration instead")
                    openAppSettings()
                    if (isBottomSheetChangeToFineLocationDialogShowing) {
                        isBottomSheetChangeToFineLocationDialogShowing = false
                        bottomSheetChangeToFineLocationDialog?.hide()
                    }
                }
            }

            binding.bottomSheetChangeToFineLocationDismissButton.setOnClickListener {
                // Carry on without fine location permission
                Log.d("permission_granted", "4. 'Carry on' button clicked in ChangeToFineLocation Bottom Sheet Dialog ")
                if (isBottomSheetChangeToFineLocationDialogShowing) {
                    Log.d("permission_granted", "4. Approximate location access granted ")
                    Log.d("permission_granted", "4. Get user approximate location asynchronously ")
                    Log.d("permission_granted", "4. Bottom sheet was showing but now will not")

                    isBottomSheetChangeToFineLocationDialogShowing = false
                    bottomSheetChangeToFineLocationDialog?.hide()
                }
            }
        }

        systemRationaleAppearanceCount = 0


        Log.d("permission_granted", "4. Entering show change to fine location permission bottom sheet")
        if (!isBottomSheetChangeToFineLocationDialogShowing) {
            Log.d("permission_granted", "4. Wasn't showing, so definitely entering")
            isBottomSheetChangeToFineLocationDialogShowing = true
            Log.d("permission_granted", "4. bottomSheetChangeToFineLocationDialog is null: " + (bottomSheetPermissionDialog == null))
            bottomSheetChangeToFineLocationDialog?.show()
        }

    }
    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestLocationPermissions() {
        // Check if the permissions are already granted
        if (!arePermissionsGranted(requiredPermissions)) {
            Log.d("permission_granted", "1. All Permissions are not granted yet: bottom sheet needed")
            Log.d("permission_granted", "1. rationaleAppearanceCount: $systemRationaleAppearanceCount")

            systemRationaleAppearanceCount = 0

            if (bottomSheetPermissionDialog == null) {
                Log.d("permission_granted", "1. First time bottom sheet creation")
                isBottomSheetPermissionDialogShowing = true
                showLocationPermissionsBottomSheet()
            } else {
                Log.d("permission_granted", "1. Bottom sheet has appeared before and show() method is being called")
                // Don't know why but when entering here for the first time,
                // the flow goes back to 3rd step and then again to 1. Permission not granted yet
                isBottomSheetPermissionDialogShowing = true
                bottomSheetPermissionDialog?.show()
            }

        } else {
            Log.d("permission_granted", "1. Permission is already granted: no bottom sheet needed")
            if (isBottomSheetPermissionDialogShowing) {
                isBottomSheetPermissionDialogShowing = false
                bottomSheetPermissionDialog?.hide()
            }
            Log.d("permission_granted", "1. Get user location asynchronously ")
        }
    }

    // This is a dialog explaining why the app needs the user to grant location permission
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showLocationPermissionsBottomSheet() {
        val binding = BottomSheetLocationPermissionBinding.inflate(layoutInflater)
        Log.d("permission_granted", "2. Entering show location permissions bottom sheet")
        bottomSheetPermissionDialog = BottomSheetDialog(requireContext()) //TODO Check requireContext() is never null
        bottomSheetPermissionDialog!!.setContentView(binding.root)

        binding.bottomSheetAllowButton.setOnClickListener {
            Log.d("permission_granted", "2. Allow button clicked in Bottom Sheet Dialog")

            if (systemRationaleAppearanceCount < 3){
                //You can arrive here even if the user presses out the system location permission dialog
                Log.d("permission_granted", "2. Should open permission request rationale")
                requestLocationPermissionLauncher.launch(requiredPermissions)
            } else {
                Log.d("permission_granted", "2. Should not open permission request rationale.Open app configuration instead")
                openAppSettings()
                if (isBottomSheetPermissionDialogShowing) {
                    isBottomSheetPermissionDialogShowing = false
                    bottomSheetPermissionDialog?.hide()
                }
            }
        }

        binding.bottomSheetDismissButton.setOnClickListener {
            // Carry on without location permission
            Log.d("permission_granted", "2. Maybe later button clicked in Bottom Sheet Dialog")
            if (isBottomSheetPermissionDialogShowing) {
                Log.d("permission_granted", "2. Bottom sheet was showing but now will not")
                isBottomSheetPermissionDialogShowing = false
                bottomSheetPermissionDialog?.hide()
            }
        }

        Log.d("permission_granted", "2. Bottom sheet dialog being displayed")
        bottomSheetPermissionDialog!!.show()
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        activity?.window?.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myLocationButton.setImageResource(R.drawable.my_location_unknown)
        binding.myLocationButton.setOnClickListener(this)
        Log.d("permission_granted", "onViewCreated | rationaleAppearanceCount: $systemRationaleAppearanceCount")
        systemRationaleAppearanceCount = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //To show normal Status Bar in other fragments
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Load map style from resource file
            val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style)
            googleMap.setMapStyle(mapStyleOptions)

            val locationValencia = LatLng(39.465421, -0.369390)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationValencia, 13f))

            // Add a marker at the initial position
            val marker = googleMap.addMarker(MarkerOptions().position(locationValencia).title("Mi ubicación"))

            // Disable rotate gestures
            googleMap.uiSettings.isRotateGesturesEnabled = false

            googleMap.uiSettings.isMyLocationButtonEnabled = true

            googleMap.setOnCameraMoveListener {
                val currentZoom = googleMap.cameraPosition.zoom
                val fraction = if (currentZoom <= 15) 0.4f else 0.3f
                // Update marker position with interpolated position
                Log.d("fraction", "$currentZoom $fraction")
                marker!!.position = updateMarkerPositionWithInterpolation(marker, fraction)
            }
        }
    }

    private fun updateMarkerPositionWithInterpolation(marker: Marker, fraction: Float): LatLng {
        val startPosition = marker.position
        val endPosition = googleMap.cameraPosition.target

        return LatLng(
            startPosition.latitude + (endPosition.latitude - startPosition.latitude) * fraction,
            startPosition.longitude + (endPosition.longitude - startPosition.longitude) * fraction
        )
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}