package com.canolabs.zonablava.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canolabs.zonablava.R
import com.canolabs.zonablava.data.source.model.DefaultParkingZonesLaVall
import com.canolabs.zonablava.data.source.model.Destination
import com.canolabs.zonablava.databinding.BottomSheetChangeToFineLocationBinding
import com.canolabs.zonablava.databinding.BottomSheetLocationPermissionBinding
import com.canolabs.zonablava.databinding.FragmentHomeBinding
import com.canolabs.zonablava.databinding.BottomSheetParkVehicleBinding
import com.canolabs.zonablava.databinding.BottomSheetMapSettingsBinding
import com.canolabs.zonablava.ui.common.Shimmer
import com.canolabs.zonablava.ui.robotoFamily
import com.canolabs.zonablava.ui.search.SearchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, View.OnClickListener {

    private val searchViewModel: SearchViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    // private var isFetchingUserLocation: Boolean = false
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    private lateinit var _parkVehicleBinding: BottomSheetParkVehicleBinding
    private lateinit var composeView: ComposeView

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    // Custom class LocationFetcher
    private lateinit var locationFetcher: LocationFetcher

    // Ensures that only one coroutine can modify the value at a time, and other coroutines will receive the
    // updated value when they access it. This CAN NOT be modified outside this class
    private val _lastKnownUserLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)

    // Expose the StateFlow to other classes as a read-only property
    // val lastKnownUserLocation: MutableStateFlow<LatLng?>

    private var markerParkCar: Marker? = null
    private val markerLastKnownUserLocation: MutableStateFlow<Marker?> = MutableStateFlow(null)

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var bottomSheetPermissionDialog: BottomSheetDialog? = null
    private var isBottomSheetPermissionDialogShowing: Boolean = false

    private var bottomSheetChangeToFineLocationDialog: BottomSheetDialog? = null
    private var isBottomSheetChangeToFineLocationDialogShowing: Boolean = false

    private var bottomSheetMapSettingsDialog: BottomSheetDialog? = null

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
                    fetchCurrentLocation()
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
                    fetchCurrentLocation()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Make status bar transparent (in some devices) and so the map occupies more screen
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

        locationFetcher = LocationFetcher(requireContext())

        binding.myLocationButton.setImageResource(R.drawable.my_location_unknown)
        binding.myLocationButton.setOnClickListener(this)

        binding.homeSettingsButton.setOnClickListener(this)

        binding.bottomSheetParkVehicleContainer
        val searchButton = binding.searchButton
        searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_search)
        }

        setupParkVehicleBottomSheet(view)
        setupParkVehicleDragHandle(view)

        Log.d("permission_granted", "onViewCreated | rationaleAppearanceCount: $systemRationaleAppearanceCount")
        systemRationaleAppearanceCount = 0
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Load map style from resource file
            val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style)
            googleMap.setMapStyle(mapStyleOptions)

            Log.d("PassResults", "onMapReady. Last marker position: ${homeViewModel.getLastMarkerDestination().location}")
            //Log.d("PassResults", "onMapReady. User selection Destination ID: ${searchViewModel.userSelection.value.placeId}")
            markerParkCar = googleMap.addMarker(
                MarkerOptions()
                    .position(homeViewModel.getLastMarkerDestination().location!!)
                    .title("Aparcar aquí")
                    .visible(true)
                    .zIndex(2.0f)
            )

            Log.d("PassResults", "onMapReady. markerParkCar position is: ${markerParkCar?.position}")

            markerLastKnownUserLocation.value = googleMap.addMarker(
                MarkerOptions()
                    .position(homeViewModel.getLastMarkerDestination().location!!)
                    .title("Mi ubicación")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_filled_bitmap))
                    .zIndex(1.0f)
                    .visible(false)
            )

            var cameraZoom = 14f
            // To position the marker to the last searched destination, in case there were any
            if (searchViewModel.userSelection.value.placeId != homeViewModel.getLastUserSearchDestination().placeId) {
                markerParkCar!!.position = searchViewModel.userSelection.value.location!!
                homeViewModel.setLastUserSearchSelection(searchViewModel.userSelection.value)
                cameraZoom = 17f
            }

            setUpLaVallParkingZones()

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerParkCar!!.position, cameraZoom))
            // Disable rotate gestures
            googleMap.uiSettings.isRotateGesturesEnabled = false

            googleMap.uiSettings.isMyLocationButtonEnabled = true

            // This line sets a listener that will be triggered when the camera position of the Google Map changes. It
            // means that whenever the user moves or zooms the map, the code inside the listener will be executed
            googleMap.setOnCameraMoveListener {
                    val currentZoom = googleMap.cameraPosition.zoom
                    val fraction = if (currentZoom <= 15) 0.4f else 0.3f
                    // Update marker position with interpolated position
                    Log.d("update_marker_position", "$currentZoom $fraction")
                    // marker should not be null if and only if the addMarker() above can not return a null
                    if (markerParkCar != null) {
                        markerParkCar!!.position = updateMarkerPositionWithInterpolation(markerParkCar!!, fraction)

                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            homeViewModel.updateMarkerPosition(markerParkCar!!.position)
                        }
                    } else {
                        // If program arrives here, maybe there was a null value when initializing marker in line googleMap.addMarker()
                        Log.e("permission_granted", "onMapReady() | For some strange reason, marker value was null")
                    }
            }
            // TODO("Find an approach to persist the map state so recreation don't need to be done")
        }
    }

    private fun setUpLaVallParkingZones() {
        googleMap.addPolygon(DefaultParkingZonesLaVall.blueZoneBenigaslo)
        googleMap.addPolygon(DefaultParkingZonesLaVall.orangeZoneCarbonaire)
    }

    private fun updateMarkerPositionWithInterpolation(marker: Marker, fraction: Float): LatLng {
        // The marker position that this functions updates is markerParkCar (not markerLastKnownUserLocation)
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

        //The onResume can be called when the user selects a suggested place in Search Fragment
        Log.d("PassResults", "Enters onResume()")
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //To show normal Status Bar in other fragments
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
        // TODO("Make the marker's last position persist")
        homeViewModel.setLastMarkerDestination(Destination("lastMarkerDestination", "none", "none", markerParkCar?.position))
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.myLocationButton -> {
                Log.d("permission_granted", "0. Button MyLocation clicked")
                if (arePermissionsGranted(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))) {
                    Log.d("permission_granted", "0. Precise location access granted ")
                    Log.d("permission_granted", "0. Get user precise location asynchronously ")
                    fetchCurrentLocation()
                    binding.myLocationButton.setImageResource(R.drawable.my_location_fill)
                } else if (arePermissionsGranted(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                    bottomSheetChangeToFineLocationDialog?.hide()
                    isBottomSheetChangeToFineLocationDialogShowing = false
                    Log.d("permission_granted", "0. Aproximate location access granted ")
                    Log.d("permission_granted", "0. Get user approximate location asynchronously")
                    fetchCurrentLocation()
                    binding.myLocationButton.setImageResource(R.drawable.my_location_aproximate)
                    showChangeToFineLocationBottomSheet()
                } else {
                    binding.myLocationButton.setImageResource(R.drawable.my_location_unknown)
                    requestLocationPermissions()
                }
            }

            R.id.homeSettingsButton -> {
                showMapSettingsBottomSheet()
            }
        }
    }

    private fun showMapSettingsBottomSheet() {
        val binding = BottomSheetMapSettingsBinding.inflate(layoutInflater)
        bottomSheetMapSettingsDialog = BottomSheetDialog(requireContext()) //TODO Check requireContext() is never null
        bottomSheetMapSettingsDialog!!.setContentView(binding.root)

        // Observe the map settings state and update the UI accordingly
        homeViewModel.mapSettingsState.onEach { state ->
            binding.bottomSheetMapSettingsToggleContainer.check(
                if (state.isWatchToggleChecked) R.id.bottom_sheet_map_settings_watch_toggle
                else R.id.bottom_sheet_map_settings_park_toggle
            )

            // Custom logic for the map based on the toggle state
            if (state.isWatchToggleChecked) {
                markerParkCar!!.isVisible = false
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else if (state.isParkToggleChecked) {
                markerParkCar!!.isVisible = true
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            binding.bottomSheetMapSettingsAllZonesSwitch.isChecked = state.isAllZonesSwitchChecked
            binding.bottomSheetMapSettingsBlueZoneSwitch.isChecked = state.isBlueZoneSwitchChecked
            binding.bottomSheetMapSettingsOrangeAndGreenZoneSwitch.isChecked =
                state.isOrangeAndGreenZoneSwitchChecked
            binding.bottomSheetMapSettingsParkingSwitch.isChecked = state.isParkingSwitchChecked
        }.launchIn(lifecycleScope) // Launches the collection of the flow in the given lifecycle scope

        binding.bottomSheetMapSettingsToggleContainer.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val currentState = homeViewModel.mapSettingsState.value // Get the current MapSettingsUIState
                val newState = when (checkedId) {
                    R.id.bottom_sheet_map_settings_watch_toggle -> {
                        currentState.copy(isWatchToggleChecked = true)
                    }
                    R.id.bottom_sheet_map_settings_park_toggle -> {
                        currentState.copy(isWatchToggleChecked = false)
                    }
                    else -> currentState.copy() // No change in state
                }

                homeViewModel.updateMapSettingsState(newState) // Update the UIState
            }
        }
        bottomSheetMapSettingsDialog!!.show()
    }

    private fun setupParkVehicleBottomSheet(view: View) {
        _parkVehicleBinding = BottomSheetParkVehicleBinding.bind(view)
        composeView = _parkVehicleBinding.composeView
        composeView.setContent {
            val address by homeViewModel.formattedAddress.collectAsState()
            if (address.isBlank()) {
                Shimmer { brush ->
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush = brush)
                    )
                }
            } else {
                // Display the address
                Text(
                    text = address,
                    fontFamily = robotoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
        // Get a reference to the bottom sheet container
        val bottomSheetContainer = view.findViewById<FrameLayout>(R.id.bottomSheetParkVehicleContainer)
        // Initialize the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        // Set the callback to listen for bottom sheet state changes
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    homeViewModel.updateMarkerPosition(markerParkCar!!.position)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle slide offset changes here if needed
            }
        })

        // Set the initial state of the bottom sheet
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setupParkVehicleDragHandle(view: View) {
        // Set a click listener on the drag handle to toggle between expanded and collapsed states
        val dragHandle = view.findViewById<View>(R.id.bottom_sheet_drag_handle)
        dragHandle.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
                    fetchCurrentLocation()

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
            fetchCurrentLocation()
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

    private fun fetchCurrentLocation() {
        //isFetchingUserLocation = true
        markerParkCar?.isVisible = false
        markerLastKnownUserLocation.value?.isVisible = false

        Log.d("permission_granted", "5. Fetching user location")

        // The failure of one location fetch doesn't affect the others;
        // shouldn't propagate the failure to the other corroutines
        val job = lifecycleScope.launch {
            supervisorScope {
                try {
                    repeatLocationFetch(3)
                } catch (e: Exception) {
                    // Handle the exception
                    Log.e("permission_granted", "5. Failed to fetch location: ${e.message}")
                }
            }
        }

        job.invokeOnCompletion {
            // Adjust marker position to match the user location (interpolation caused a deviated position)
            if (markerLastKnownUserLocation.value?.position != null) {
                markerParkCar?.position = markerLastKnownUserLocation.value?.position!!
                Log.d("permission_granted", "5. MarkerParkCar location: ${markerParkCar?.position}")
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_lastKnownUserLocation.value!!, 16f))

            markerParkCar?.isVisible = true
            markerLastKnownUserLocation.value?.isVisible = true
        }
    }

    private suspend fun repeatLocationFetch(iterations: Int) {
        // Please ensure that iterations is not too high since this is a recursive function

        /*
           It's important to note that multiple coroutines may execute concurrently this function
           Potentially race conditions should not occur in 'userLocation' variable because of using StateFlow
           'mostRecentUserLocation' variable should not also be a problem since every thread creates its own variable
           If there are any other shared resources or mutable state outside the scope
           of the coroutine, proper synchronization mechanisms should be used to ensure thread safety.
        */

        if (iterations <= 0) {
            //isFetchingUserLocation = false
            return
        }
        Log.d("permission_granted", "6. Launching coroutine")

        val mostRecentUserLocation = locationFetcher.fetchLocation()
        Log.d("permission_granted", "6. Most Recent User Location | Latitude: ${mostRecentUserLocation.latitude}, Longitude: ${mostRecentUserLocation.longitude}")
        Log.d("permission_granted", "6. Last Known User Location Stored in StateFlow | Latitude: ${_lastKnownUserLocation.value?.latitude}, Longitude: ${_lastKnownUserLocation.value?.longitude}")

        if (_lastKnownUserLocation.value != mostRecentUserLocation) {
            _lastKnownUserLocation.value = mostRecentUserLocation
            markerLastKnownUserLocation.value?.position = mostRecentUserLocation

            // Point the park marker to the last user locaton position
            markerParkCar?.position = mostRecentUserLocation
        }

        // Following delay could be omitted because it's a recursive function,
        // but be aware of performance implications and computational load
        delay(300)
        repeatLocationFetch(iterations - 1)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}