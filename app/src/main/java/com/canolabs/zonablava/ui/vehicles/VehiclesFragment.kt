package com.canolabs.zonablava.ui.vehicles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.canolabs.zonablava.databinding.FragmentVehiclesBinding

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

        val textView: TextView = binding.textVehicles
        vehiclesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}