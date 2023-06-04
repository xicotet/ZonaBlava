package com.canolabs.zonablava.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

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

            googleMap.setOnCameraMoveListener {
                // Update marker position with interpolated position
                marker!!.position = updateMarkerPositionWithInterpolation(marker, 0.3f)
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