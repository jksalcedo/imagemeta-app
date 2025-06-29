package com.jksalcedo.imagemeta.ui.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jksalcedo.imagemeta.databinding.ItemExifEditBinding

class ExifAdapter(private val exifDataList: MutableList<ExifData>) :
    RecyclerView.Adapter<ExifAdapter.ExifViewHolder>() {

    inner class ExifViewHolder(val binding: ItemExifEditBinding) : RecyclerView.ViewHolder(binding.root) {
        // A TextWatcher to update the data model when the user edits the text
        private var textWatcher: TextWatcher? = null

        fun bind(exifData: ExifData) {
            // Remove any existing watcher to prevent unwanted triggers during recycling
            binding.etExifValue.removeTextChangedListener(textWatcher)

            // Set the label as a hint and the value as the text
            binding.ilExifValue.hint = exifData.label
            binding.etExifValue.setText(exifData.value)

            // Create and attach a new watcher
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Update the value in our data list as the user types
                    exifDataList[adapterPosition].value = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
            binding.etExifValue.addTextChangedListener(textWatcher)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExifViewHolder {
        val binding = ItemExifEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExifViewHolder, position: Int) {
        holder.bind(exifDataList[position])
    }

    override fun getItemCount(): Int = exifDataList.size

    // Helper function to get the current state of the data
    fun getCurrentData(): List<ExifData> = exifDataList
}