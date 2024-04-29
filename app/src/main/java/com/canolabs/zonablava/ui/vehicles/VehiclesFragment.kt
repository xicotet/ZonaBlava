package com.canolabs.zonablava.ui.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.canolabs.zonablava.R
import com.canolabs.zonablava.databinding.FragmentVehiclesBinding
import com.canolabs.zonablava.ui.ezraFamily

class VehiclesFragment : Fragment() {

    private var _binding: FragmentVehiclesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vehiclesViewModel =
            ViewModelProvider(this)[VehiclesViewModel::class.java]

        _binding = FragmentVehiclesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val composeView = binding.composeView
        composeView.setContent {
            val uiState by vehiclesViewModel.state.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(id = R.string.title_vehicles),
                    fontSize = 42.sp,
                    fontFamily = ezraFamily,
                )

                Spacer(modifier = Modifier.height(32.dp))

                ElevatedButton(
                    onClick = { /* Handle button click here */ },
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = colorResource(id = R.color.md_theme_light_inverseOnSurface),
                        contentColor = colorResource(id = R.color.md_theme_light_onSecondaryContainer),
                    ),
                    border = BorderStroke(1.dp, colorResource(id = R.color.md_theme_light_onSecondaryContainer))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, colorResource(id = R.color.md_theme_light_onSecondaryContainer)),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add vehicle icon",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.vehicles_add_vehicle_button))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                uiState.vehicles.forEach { vehicle ->
                    VehicleCard(vehicle = vehicle)
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}