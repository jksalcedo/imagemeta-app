package com.jksalcedo.imagemeta.ui.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jksalcedo.imagemeta.databinding.ItemExifEditBinding
import com.jksalcedo.imagemeta.databinding.ItemMetadataCategoryBinding

class EnhancedMetadataAdapter(
    private var metadataList: MutableList<Any> // Can contain CategoryHeader or EnhancedMetadata
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CATEGORY_HEADER = 0
        private const val TYPE_METADATA_ITEM = 1
    }

    data class CategoryHeader(val category: MetadataCategory)

    inner class CategoryHeaderViewHolder(val binding: ItemMetadataCategoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(header: CategoryHeader) {
            binding.categoryTitle.text = header.category.displayName
        }
    }

    inner class MetadataViewHolder(val binding: ItemExifEditBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        private var textWatcher: TextWatcher? = null

        fun bind(metadata: EnhancedMetadata) {
            // Remove any existing watcher to prevent unwanted triggers during recycling
            binding.etExifValue.removeTextChangedListener(textWatcher)

            // Set the label as a hint and the value as the text
            binding.ilExifValue.hint = metadata.label
            binding.etExifValue.setText(metadata.value)
            
            // Show format indicator
            binding.formatIndicator?.text = metadata.format.name
            binding.formatIndicator?.visibility = if (metadata.format != MetadataFormat.EXIF) View.VISIBLE else View.GONE

            // Enable/disable editing based on metadata properties
            binding.etExifValue.isEnabled = metadata.isEditable

            // Create and attach a new watcher
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Update the value in our data list as the user types
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION && metadataList[position] is EnhancedMetadata) {
                        (metadataList[position] as EnhancedMetadata).value = s.toString()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }
            binding.etExifValue.addTextChangedListener(textWatcher)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (metadataList[position]) {
            is CategoryHeader -> TYPE_CATEGORY_HEADER
            is EnhancedMetadata -> TYPE_METADATA_ITEM
            else -> TYPE_METADATA_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY_HEADER -> {
                val binding = ItemMetadataCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CategoryHeaderViewHolder(binding)
            }
            TYPE_METADATA_ITEM -> {
                val binding = ItemExifEditBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                MetadataViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryHeaderViewHolder -> holder.bind(metadataList[position] as CategoryHeader)
            is MetadataViewHolder -> holder.bind(metadataList[position] as EnhancedMetadata)
        }
    }

    override fun getItemCount(): Int = metadataList.size

    // Helper function to get the current metadata (excluding headers)
    fun getCurrentMetadata(): List<EnhancedMetadata> {
        return metadataList.filterIsInstance<EnhancedMetadata>()
    }
    
    // Update the adapter with new grouped metadata
    fun updateMetadata(metadata: List<EnhancedMetadata>) {
        val groupedItems = mutableListOf<Any>()
        
        // Group metadata by category
        val groupedMetadata = metadata.groupBy { it.category }
        
        // Add categories and their metadata in order
        for (category in EnhancedExifTags.getAllCategories()) {
            val categoryMetadata = groupedMetadata[category]
            if (!categoryMetadata.isNullOrEmpty()) {
                groupedItems.add(CategoryHeader(category))
                groupedItems.addAll(categoryMetadata)
            }
        }
        
        metadataList.clear()
        metadataList.addAll(groupedItems)
        notifyDataSetChanged()
    }
}