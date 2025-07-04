package com.jksalcedo.imagemeta.ui.edit

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jksalcedo.imagemeta.databinding.FragmentEditBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args: EditFragmentArgs by navArgs()

    private var originalImageUri: Uri? = null
    private lateinit var enhancedMetadataAdapter: EnhancedMetadataAdapter
    private lateinit var metadataExtractor: EnhancedMetadataExtractor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originalImageUri = args.imageUri.toUri()
        
        metadataExtractor = EnhancedMetadataExtractor(requireContext())
        setupRecyclerView()
        loadAllMetadata()

        binding.saveButton.setOnClickListener {
            saveMetadataAsNewCopy()
        }
    }

    private fun setupRecyclerView() {
        enhancedMetadataAdapter = EnhancedMetadataAdapter(mutableListOf())
        binding.exifRecyclerView.adapter = enhancedMetadataAdapter
    }

    private fun loadAllMetadata() {
        binding.exifRecyclerView.isVisible = false
        originalImageUri?.let { uri ->
            try {
                // Load the image into the ImageView for Preview
                binding.selectedImageView.setImageURI(uri)

                // Extract all metadata using enhanced extractor
                val allMetadata = metadataExtractor.extractAllMetadata(uri)
                
                if (allMetadata.isEmpty()) {
                    Toast.makeText(requireContext(), "No readable metadata found.", Toast.LENGTH_LONG).show()
                } else {
                    enhancedMetadataAdapter.updateMetadata(allMetadata)
                }
                
            } catch (e: Exception) {
                Log.e("EditFragment", "Error loading metadata", e)
                Toast.makeText(requireContext(), "Could not load image data.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.exifRecyclerView.isVisible = true
    }

    private fun saveMetadataAsNewCopy() {
        var success = false
        val resolver = requireContext().contentResolver

        // Create a new image entry in the MediaStore
        val newImageDetails = ContentValues().apply {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_EDITED_$timestamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            // This line will save the image in the Pictures/ImageMeta directory
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ImageMeta")
            }
        }

        // new file
        val newImageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newImageDetails)

        if (newImageUri == null) {
            Toast.makeText(requireContext(), "Failed to create new image file.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // Copy the original image data to the new URI
            originalImageUri?.let { sourceUri ->
                resolver.openInputStream(sourceUri)?.use { inputStream ->
                    resolver.openOutputStream(newImageUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            // modify the metadata of the NEW file
            resolver.openFileDescriptor(newImageUri, "rw")?.use { pfd ->
                val exif = ExifInterface(pfd.fileDescriptor)
                val updatedMetadata = enhancedMetadataAdapter.getCurrentMetadata()

                // Only save EXIF metadata (other formats like IPTC/XMP require different handling)
                for (item in updatedMetadata) {
                    if (item.format == MetadataFormat.EXIF && item.isEditable) {
                        exif.setAttribute(item.tag, item.value)
                    }
                }
                exif.saveAttributes()
                success = true
                Toast.makeText(requireContext(), "New image saved to Pictures/ImageMeta", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("EditFragment", "Error saving new image with EXIF data", e)
            Toast.makeText(requireContext(), "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
            // If something fails, delete the partially created image
            resolver.delete(newImageUri, null, null)
            success = false
        }

        val action = EditFragmentDirections.actionEditToHome(
            success,
            imageUri = null
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// object to define all the tags
object ExifTags {
    val allTags = mapOf(
        ExifInterface.TAG_MAKE to "Make",
        ExifInterface.TAG_MODEL to "Model",
        ExifInterface.TAG_ARTIST to "Artist",
        ExifInterface.TAG_DATETIME to "Date Time",
        ExifInterface.TAG_DATETIME_ORIGINAL to "Date Time Original",
        ExifInterface.TAG_DATETIME_DIGITIZED to "Date Time Digitized",
        ExifInterface.TAG_IMAGE_DESCRIPTION to "Description",
        ExifInterface.TAG_USER_COMMENT to "User Comment",
        ExifInterface.TAG_SOFTWARE to "Software",
        ExifInterface.TAG_F_NUMBER to "F-Number (Aperture)",
        ExifInterface.TAG_FOCAL_LENGTH to "Focal Length",
        ExifInterface.TAG_ISO_SPEED_RATINGS to "ISO Speed",
        ExifInterface.TAG_EXPOSURE_TIME to "Exposure Time",
        ExifInterface.TAG_GPS_LATITUDE to "GPS Latitude",
        ExifInterface.TAG_GPS_LONGITUDE to "GPS Longitude",
        ExifInterface.TAG_GPS_ALTITUDE to "GPS Altitude",
        ExifInterface.TAG_GPS_PROCESSING_METHOD to "GPS Processing Method",
        ExifInterface.TAG_GPS_DATESTAMP to "GPS Date Stamp"
        // any other tags
    )
}