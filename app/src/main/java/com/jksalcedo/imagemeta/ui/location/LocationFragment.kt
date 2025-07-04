package com.jksalcedo.imagemeta.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.jksalcedo.imagemeta.databinding.FragmentLocationBinding

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val args: LocationFragmentArgs by navArgs()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Parse GPS coordinates from arguments
        latitude = args.latitude.toDoubleOrNull() ?: 0.0
        longitude = args.longitude.toDoubleOrNull() ?: 0.0
        
        setupLocationDisplay()
        setupEditableCoordinates()
    }

    private fun setupLocationDisplay() {
        if (latitude != 0.0 && longitude != 0.0) {
            binding.coordinatesText.text = "Latitude: $latitude\nLongitude: $longitude"
            binding.mapPlaceholder.text = "📍 Location: $latitude, $longitude\n\n(Map integration would go here)\n\nIn a production app, this would show:\n• Interactive Google Maps view\n• Marker at current location\n• Ability to drag marker to update coordinates\n• Nearby places and addresses"
        } else {
            binding.coordinatesText.text = "No GPS coordinates found"
            binding.mapPlaceholder.text = "📍 No location data available\n\nTo add location:\n1. Enter coordinates manually below\n2. Or use a map picker (feature coming soon)"
        }
    }

    private fun setupEditableCoordinates() {
        // Pre-fill with existing coordinates
        if (latitude != 0.0) binding.latitudeEdit.setText(latitude.toString())
        if (longitude != 0.0) binding.longitudeEdit.setText(longitude.toString())
        
        binding.updateLocationButton.setOnClickListener {
            val newLat = binding.latitudeEdit.text.toString().toDoubleOrNull()
            val newLng = binding.longitudeEdit.text.toString().toDoubleOrNull()
            
            if (newLat != null && newLng != null) {
                if (newLat >= -90 && newLat <= 90 && newLng >= -180 && newLng <= 180) {
                    latitude = newLat
                    longitude = newLng
                    setupLocationDisplay()
                    Toast.makeText(requireContext(), "Location updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Invalid coordinates range", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter valid numeric coordinates", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.clearLocationButton.setOnClickListener {
            latitude = 0.0
            longitude = 0.0
            binding.latitudeEdit.setText("")
            binding.longitudeEdit.setText("")
            setupLocationDisplay()
            Toast.makeText(requireContext(), "Location cleared", Toast.LENGTH_SHORT).show()
        }
    }

    // Public method to get updated coordinates (can be used by parent fragments)
    fun getUpdatedCoordinates(): Pair<Double, Double> {
        return Pair(latitude, longitude)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}