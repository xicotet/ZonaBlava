package com.canolabs.zonablava.ui.vehicles.addVehicle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import com.canolabs.zonablava.R
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.canolabs.zonablava.databinding.FragmentAddVehicleBinding
import com.canolabs.zonablava.ui.ezraFamily
import com.canolabs.zonablava.ui.robotoFamily
import java.util.Locale

class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addVehicleViewModel =
            ViewModelProvider(this)[AddVehicleViewModel::class.java]
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)

        val composeView = binding.composeView
        composeView.setContent {
            val uiState by addVehicleViewModel.state.collectAsState()
            val scrollBehavior =
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
            val scrollState = rememberScrollState()

            val collapsedFraction = scrollBehavior.state.collapsedFraction
            val fontSize = if (collapsedFraction > 0.5) {
                24.sp // Smaller font size when the AppBar is more than half collapsed
            } else {
                34.sp // Larger font size when the AppBar is less than half collapsed
            }

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        title = {
                            Text(
                                stringResource(id = R.string.title_add_vehicle),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = fontSize,
                                fontFamily = ezraFamily
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { findNavController().navigate(R.id.action_navigation_add_vehicle_to_navigation_vehicles) },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = "Localized description"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.license_empty),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        TextField(
                            value = uiState.licensePlate,
                            onValueChange = { value ->
                                addVehicleViewModel.onLicensePlateChanged(value.uppercase(Locale.ROOT))
                                addVehicleViewModel.onEvent(AddVehicleUIEvent.ValidateLicensePlate)
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.vehicles_license_plate_placeholder),
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = colorResource(id = R.color.md_theme_light_primary)
                            ),
                            textStyle = TextStyle(
                                fontSize = 32.sp,
                                fontFamily = ezraFamily,
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .padding(2.dp)
                                .size(300.dp, 80.dp)
                                .align(Alignment.TopCenter),
                        )
                    }
                    if (uiState.licensePlateErrorId != null) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Caution icon",
                                tint = colorResource(id = R.color.md_theme_light_error),
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = stringResource(id = uiState.licensePlateErrorId!!),
                                color = colorResource(id = R.color.md_theme_light_error),
                                modifier = Modifier.padding()
                            )
                        }
                    }
                    OutlinedTextField(
                        value = uiState.brand,
                        onValueChange = { addVehicleViewModel.onBrandChanged(it) },
                        label = { Text(stringResource(id = R.string.vehicles_vehicle_brand)) },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = robotoFamily,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = colorResource(id = R.color.md_theme_light_primary),
                            focusedBorderColor = colorResource(id = R.color.md_theme_light_primary),
                            unfocusedBorderColor = colorResource(id = R.color.md_theme_light_onSurface),
                            cursorColor = colorResource(id = R.color.md_theme_light_primary),
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = uiState.model,
                        onValueChange = { addVehicleViewModel.onModelChanged(it) },
                        label = { Text(stringResource(id = R.string.vehicles_vehicle_model)) },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = robotoFamily,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = colorResource(id = R.color.md_theme_light_primary),
                            focusedBorderColor = colorResource(id = R.color.md_theme_light_primary),
                            unfocusedBorderColor = colorResource(id = R.color.md_theme_light_onSurface),
                            cursorColor = colorResource(id = R.color.md_theme_light_primary),
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = uiState.alias,
                        onValueChange = { addVehicleViewModel.onAliasChanged(it) },
                        label = { Text(stringResource(id = R.string.vehicles_vehicle_alias)) },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = robotoFamily,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedLabelColor = colorResource(id = R.color.md_theme_light_primary),
                            focusedBorderColor = colorResource(id = R.color.md_theme_light_primary),
                            unfocusedBorderColor = colorResource(id = R.color.md_theme_light_onSurface),
                            cursorColor = colorResource(id = R.color.md_theme_light_primary),
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .align(Alignment.Start)
                            .height(80.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    var selectedIndex by remember { mutableIntStateOf(0) }
                    val options = listOf(
                        R.drawable.car_normal,
                        R.drawable.motorcycle,
                        R.drawable.local_shipping
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        options.forEachIndexed { index, iconRes ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                onClick = { selectedIndex = index },
                                selected = index == selectedIndex,
                                modifier = Modifier.height(80.dp),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = colorResource(id = R.color.md_theme_light_tertiaryContainer),
                                ),
                            ) {
                                Icon(
                                    painterResource(iconRes),
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = null)
                            }
                        }
                    }

                    ElevatedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.md_theme_light_surfaceVariant),
                            contentColor = colorResource(id = R.color.md_theme_light_error),
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.vehicles_delete_vehicle),
                            fontSize = 16.sp,
                            fontFamily = robotoFamily
                        )
                    }

                    ElevatedButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.md_theme_light_scrim),
                            contentColor = colorResource(id = R.color.md_theme_light_onPrimary),
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.vehicles_save_vehicle),
                            fontSize = 16.sp,
                            fontFamily = robotoFamily
                        )
                    }
                }
            }

        }
        return binding.root
    }
}