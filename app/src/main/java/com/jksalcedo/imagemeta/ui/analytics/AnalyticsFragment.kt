package com.jksalcedo.imagemeta.ui.analytics

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.jksalcedo.imagemeta.databinding.FragmentAnalyticsBinding
import com.jksalcedo.imagemeta.ui.edit.EnhancedMetadataExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val args: AnalyticsFragmentArgs by navArgs()

    private lateinit var metadataExtractor: EnhancedMetadataExtractor
    private lateinit var imageUris: List<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        imageUris = args.imageUris.map { Uri.parse(it) }
        metadataExtractor = EnhancedMetadataExtractor(requireContext())
        
        binding.imageCountText.text = "Analyzing ${imageUris.size} images"
        
        loadAnalytics()
    }

    private fun loadAnalytics() {
        binding.progressBar.isVisible = true
        binding.chartsContainer.isVisible = false
        
        CoroutineScope(Dispatchers.IO).launch {
            val cameraModels = mutableMapOf<String, Int>()
            val isoValues = mutableMapOf<String, Int>()
            val apertures = mutableMapOf<String, Int>()
            val hasGpsCount = mutableMapOf<String, Int>("With GPS" to 0, "Without GPS" to 0)
            
            // Analyze metadata from all images
            for (uri in imageUris) {
                try {
                    val metadata = metadataExtractor.extractAllMetadata(uri)
                    
                    // Count camera models
                    val cameraModel = metadata.find { it.tag.contains("MODEL") }?.value?.takeIf { it.isNotEmpty() } ?: "Unknown"
                    cameraModels[cameraModel] = cameraModels.getOrDefault(cameraModel, 0) + 1
                    
                    // Count ISO values
                    val iso = metadata.find { it.tag.contains("ISO") }?.value?.takeIf { it.isNotEmpty() } ?: "Unknown"
                    isoValues[iso] = isoValues.getOrDefault(iso, 0) + 1
                    
                    // Count aperture values
                    val aperture = metadata.find { it.tag.contains("F_NUMBER") }?.value?.takeIf { it.isNotEmpty() } ?: "Unknown"
                    apertures[aperture] = apertures.getOrDefault(aperture, 0) + 1
                    
                    // Count GPS presence
                    val hasGps = metadata.any { 
                        (it.tag.contains("GPS_LATITUDE") || it.tag.contains("GPS_LONGITUDE")) && it.value.isNotEmpty() 
                    }
                    if (hasGps) {
                        hasGpsCount["With GPS"] = hasGpsCount.getOrDefault("With GPS", 0) + 1
                    } else {
                        hasGpsCount["Without GPS"] = hasGpsCount.getOrDefault("Without GPS", 0) + 1
                    }
                    
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            withContext(Dispatchers.Main) {
                setupCameraModelChart(cameraModels)
                setupIsoChart(isoValues)
                setupGpsChart(hasGpsCount)
                
                binding.progressBar.isVisible = false
                binding.chartsContainer.isVisible = true
            }
        }
    }

    private fun setupCameraModelChart(data: Map<String, Int>) {
        val pieChart: PieChart = binding.cameraModelChart
        val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
        
        val dataSet = PieDataSet(entries, "Camera Models")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Camera Models"
        pieChart.setCenterTextSize(16f)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun setupIsoChart(data: Map<String, Int>) {
        val barChart: BarChart = binding.isoChart
        val entries = data.entries.mapIndexed { index, entry -> 
            BarEntry(index.toFloat(), entry.value.toFloat()) 
        }
        
        val dataSet = BarDataSet(entries, "ISO Values")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextSize = 12f
        
        val barData = BarData(dataSet)
        barChart.data = barData
        
        // Setup X-axis labels
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(data.keys.toList())
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun setupGpsChart(data: Map<String, Int>) {
        val pieChart: PieChart = binding.gpsChart
        val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
        
        val dataSet = PieDataSet(entries, "GPS Data")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK
        
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "GPS Availability"
        pieChart.setCenterTextSize(16f)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}