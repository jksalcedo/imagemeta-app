package com.jksalcedo.imagemeta.ui.edit

// Enum to represent different metadata categories for better organization
enum class MetadataCategory(val displayName: String) {
    BASIC_INFO("Basic Information"),
    CAMERA_SETTINGS("Camera Settings"), 
    DATE_TIME("Date & Time"),
    LOCATION("Location Data"),
    TECHNICAL("Technical Details"),
    USER_DATA("User Information")
}

// Enhanced data class to support categorized metadata
data class EnhancedMetadata(
    val tag: String,
    val label: String,
    var value: String,
    val category: MetadataCategory,
    val format: MetadataFormat = MetadataFormat.EXIF,
    val isEditable: Boolean = true
)

// Enum for different metadata formats
enum class MetadataFormat {
    EXIF,
    IPTC,
    XMP
}