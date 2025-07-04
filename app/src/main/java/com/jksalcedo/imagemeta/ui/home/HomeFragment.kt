package com.jksalcedo.imagemeta.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.jksalcedo.imagemeta.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var photoUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val action = HomeFragmentDirections.actionHomeToEdit(it.toString())
            findNavController().navigate(action)
        }
    }

    private val selectMultipleImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val uriStrings = uris.map { it.toString() }.toTypedArray()
            // Navigate to batch edit or analytics based on user choice
            // For now, we'll show a simple selection (this would be improved with a proper dialog)
            if (uris.size == 1) {
                val action = HomeFragmentDirections.actionHomeToEdit(uris[0].toString())
                findNavController().navigate(action)
            } else {
                // Navigate to batch edit for multiple images
                // This would need to be implemented in navigation args
                // val action = HomeFragmentDirections.actionHomeToBatchEdit(uriStrings)
                // findNavController().navigate(action)
            }
        }
    }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && photoUri != null) {
                val action = HomeFragmentDirections.actionHomeToEdit(photoUri.toString())
                findNavController().navigate(action)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.footerTextView.text = buildString { BuildConfig.VERSION_NAME?.let { append(it) } }

        binding.selectImageCard.setOnClickListener {
            // Launch the image picker to select an image
            selectImageLauncher.launch("image/*")
        }

        binding.takePhotoCard.setOnClickListener {
            val photoFile = File.createTempFile(
                "IMG_${System.currentTimeMillis()}_",
                ".jpg",
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            takePhotoLauncher.launch(intent)
        }

        binding.batchOperationsCard.setOnClickListener {
            // Launch multi-select for batch operations
            selectMultipleImagesLauncher.launch("image/*")
        }

        binding.analyticsCard.setOnClickListener {
            // Launch multi-select for analytics
            selectMultipleImagesLauncher.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}