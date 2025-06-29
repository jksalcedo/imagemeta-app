package com.jksalcedo.imagemeta.ui.edit

// A data class to represent a single piece of EXIF metadata
data class ExifData(
    val tag: String,      // The ExifInterface constant, e.g., ExifInterface.TAG_MAKE
    val label: String,    // A user-friendly label, e.g., "Camera Make"
    var value: String     // The value of the tag, e.g., "Google"
)