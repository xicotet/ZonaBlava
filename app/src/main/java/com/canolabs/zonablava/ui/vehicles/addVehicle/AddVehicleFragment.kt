package com.canolabs.zonablava.ui.vehicles.addVehicle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import com.canolabs.zonablava.R
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.canolabs.zonablava.databinding.FragmentAddVehicleBinding
import com.canolabs.zonablava.ui.ezraFamily

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
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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
                                onClick = { /* do something */ },
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
                        .verticalScroll(scrollState)
                ) {
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                    Text(modifier = Modifier.padding(innerPadding), text = "Hello, World!", fontSize = 48.sp)
                }
            }

        }
        return binding.root
    }
}