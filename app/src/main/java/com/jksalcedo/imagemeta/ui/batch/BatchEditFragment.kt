package com.jksalcedo.imagemeta.ui.batch

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jksalcedo.imagemeta.databinding.FragmentBatchEditBinding
import com.jksalcedo.imagemeta.ui.edit.EnhancedMetadata
import com.jksalcedo.imagemeta.ui.edit.EnhancedMetadataAdapter
import com.jksalcedo.imagemeta.ui.edit.EnhancedMetadataExtractor
import com.jksalcedo.imagemeta.ui.edit.MetadataFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BatchEditFragment : Fragment() {

    private var _binding: FragmentBatchEditBinding? = null
    private val binding get() = _binding!!
    private val args: BatchEditFragmentArgs by navArgs()

    private lateinit var imageUris: List<Uri>
    private lateinit var templateMetadataAdapter: EnhancedMetadataAdapter
    private lateinit var metadataExtractor: EnhancedMetadataExtractor
    private var commonMetadata: List<EnhancedMetadata> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatchEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Parse the image URIs from arguments
        imageUris = args.imageUris.map { Uri.parse(it) }
        
        metadataExtractor = EnhancedMetadataExtractor(requireContext())
        
        setupRecyclerView()
        loadCommonMetadata()
        
        binding.applyToAllButton.setOnClickListener {
            applyMetadataToAllImages()
        }
        
        binding.imageCountText.text = "Editing ${imageUris.size} images"
    }

    private fun setupRecyclerView() {
        templateMetadataAdapter = EnhancedMetadataAdapter(mutableListOf())
        binding.metadataRecyclerView.apply {
            adapter = templateMetadataAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadCommonMetadata() {
        binding.progressBar.isVisible = true
        binding.metadataRecyclerView.isVisible = false
        
        // Extract metadata from first image as template
        if (imageUris.isNotEmpty()) {
            try {
                val firstImageMetadata = metadataExtractor.extractAllMetadata(imageUris[0])
                
                // Filter to show only commonly editable metadata
                commonMetadata = firstImageMetadata.filter { metadata ->
                    metadata.isEditable && (
                        metadata.tag.contains("ARTIST") ||
                        metadata.tag.contains("DESCRIPTION") ||
                        metadata.tag.contains("COMMENT") ||
                        metadata.tag.contains("COPYRIGHT") ||
                        metadata.tag.contains("GPS") ||
                        metadata.tag.contains("DATETIME")
                    )
                }
                
                templateMetadataAdapter.updateMetadata(commonMetadata)
                
            } catch (e: Exception) {
                Log.e("BatchEditFragment", "Error loading metadata", e)
                Toast.makeText(requireContext(), "Error loading metadata template", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.progressBar.isVisible = false
        binding.metadataRecyclerView.isVisible = true
    }

    private fun applyMetadataToAllImages() {
        binding.progressBar.isVisible = true
        binding.applyToAllButton.isEnabled = false
        
        val updatedMetadata = templateMetadataAdapter.getCurrentMetadata()
        val resolver = requireContext().contentResolver
        var successCount = 0
        var errorCount = 0
        
        for (imageUri in imageUris) {
            try {
                // Create a new image copy with updated metadata
                val newImageDetails = ContentValues().apply {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val randomId = (1000..9999).random()
                    put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_BATCH_${timestamp}_$randomId.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ImageMeta/Batch")
                    }
                }

                val newImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newImageDetails)
                
                if (newImageUri != null) {
                    // Copy original image data
                    resolver.openInputStream(imageUri)?.use { inputStream ->
                        resolver.openOutputStream(newImageUri)?.use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // Apply metadata
                    resolver.openFileDescriptor(newImageUri, "rw")?.use { pfd ->
                        val exif = ExifInterface(pfd.fileDescriptor)
                        
                        for (metadata in updatedMetadata) {
                            if (metadata.format == MetadataFormat.EXIF && 
                                metadata.value.isNotEmpty()) {
                                exif.setAttribute(metadata.tag, metadata.value)
                            }
                        }
                        exif.saveAttributes()
                    }
                    
                    successCount++
                } else {
                    errorCount++
                }
                
            } catch (e: Exception) {
                Log.e("BatchEditFragment", "Error processing image: $imageUri", e)
                errorCount++
            }
        }
        
        binding.progressBar.isVisible = false
        binding.applyToAllButton.isEnabled = true
        
        val message = "Batch processing complete!\nSuccess: $successCount\nErrors: $errorCount"
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        
        // Navigate back to home
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}