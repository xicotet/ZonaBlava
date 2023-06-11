package com.canolabs.zonablava.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canolabs.zonablava.BuildConfig
import com.canolabs.zonablava.R
import com.canolabs.zonablava.data.source.model.DefaultPlaces
import com.canolabs.zonablava.data.source.model.Place
import com.canolabs.zonablava.databinding.FragmentSearchBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private lateinit var placesClient: PlacesClient

    private var searchJob: Job? = null // Keep track of the search coroutine job

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the SDK and create a new PlacesClient instance
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(requireContext())

        setupSearchTextInput()
        setupSuggestionsRecyclerView()

        return root
    }

    private fun setupSearchTextInput() {
        val searchInputLayout: TextInputLayout = binding.searchInputLayout
        val searchEditText: TextInputEditText = binding.searchEditText
        val searchPlaceholder: TextView = binding.searchPlaceholder

        searchEditText.requestFocus()

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

            } else {
                if (searchEditText.text.isNullOrBlank()) {

                }
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(searchEditable: Editable?) {
                val searchQuery = searchEditable.toString()
                if (searchQuery.isNullOrBlank()) {
                    searchPlaceholder.visibility = View.VISIBLE
                } else {
                    searchPlaceholder.visibility = View.INVISIBLE
                }

                searchViewModel.setSearchQuery(searchQuery)
            }
        })
    }

    private fun setupSuggestionsRecyclerView() {
        val searchRecyclerView: RecyclerView = binding.searchRecyclerView

        val defaultCities = arrayListOf<Place>(
            DefaultPlaces.BETXI,
            DefaultPlaces.LAVALL,
            DefaultPlaces.VALENCIA,
            DefaultPlaces.BENIDORM
        )

        adapter = SearchAdapter(defaultCities) { selectedSuggestion ->
            Log.d("PassResults", "Adapter detects user selection: ${selectedSuggestion.placeId}")
            searchViewModel.setUserSelection(selectedSuggestion)
            findNavController().navigate(R.id.action_navigation_search_to_navigation_home)
        }

        // Set up the RecyclerView with the necessary adapter and layout manager
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = adapter

        // Observe the search results from the view model
        searchViewModel.searchResults.observe(viewLifecycleOwner) { suggestions ->
            adapter.setSuggestions(suggestions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}