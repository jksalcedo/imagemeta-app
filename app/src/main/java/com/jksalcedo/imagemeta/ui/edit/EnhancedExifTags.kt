package com.jksalcedo.imagemeta.ui.edit

import androidx.exifinterface.media.ExifInterface

// Enhanced ExifTags object with categorization
object EnhancedExifTags {
    val categorizedTags = mapOf(
        // Basic Information
        ExifInterface.TAG_MAKE to Pair("Camera Make", MetadataCategory.BASIC_INFO),
        ExifInterface.TAG_MODEL to Pair("Camera Model", MetadataCategory.BASIC_INFO),
        ExifInterface.TAG_SOFTWARE to Pair("Software", MetadataCategory.BASIC_INFO),
        ExifInterface.TAG_IMAGE_WIDTH to Pair("Image Width", MetadataCategory.BASIC_INFO),
        ExifInterface.TAG_IMAGE_LENGTH to Pair("Image Height", MetadataCategory.BASIC_INFO),
        ExifInterface.TAG_ORIENTATION to Pair("Orientation", MetadataCategory.BASIC_INFO),
        
        // Camera Settings
        ExifInterface.TAG_F_NUMBER to Pair("F-Number (Aperture)", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_FOCAL_LENGTH to Pair("Focal Length", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_ISO_SPEED_RATINGS to Pair("ISO Speed", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_EXPOSURE_TIME to Pair("Exposure Time", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_APERTURE_VALUE to Pair("Aperture Value", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_SHUTTER_SPEED_VALUE to Pair("Shutter Speed", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_EXPOSURE_MODE to Pair("Exposure Mode", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_WHITE_BALANCE to Pair("White Balance", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_FLASH to Pair("Flash", MetadataCategory.CAMERA_SETTINGS),
        ExifInterface.TAG_METERING_MODE to Pair("Metering Mode", MetadataCategory.CAMERA_SETTINGS),
        
        // Date & Time
        ExifInterface.TAG_DATETIME to Pair("Date Time", MetadataCategory.DATE_TIME),
        ExifInterface.TAG_DATETIME_ORIGINAL to Pair("Date Time Original", MetadataCategory.DATE_TIME),
        ExifInterface.TAG_DATETIME_DIGITIZED to Pair("Date Time Digitized", MetadataCategory.DATE_TIME),
        ExifInterface.TAG_GPS_DATESTAMP to Pair("GPS Date Stamp", MetadataCategory.DATE_TIME),
        ExifInterface.TAG_GPS_TIMESTAMP to Pair("GPS Time Stamp", MetadataCategory.DATE_TIME),
        
        // Location Data
        ExifInterface.TAG_GPS_LATITUDE to Pair("GPS Latitude", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_LONGITUDE to Pair("GPS Longitude", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_ALTITUDE to Pair("GPS Altitude", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_ALTITUDE_REF to Pair("GPS Altitude Reference", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_LATITUDE_REF to Pair("GPS Latitude Reference", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_LONGITUDE_REF to Pair("GPS Longitude Reference", MetadataCategory.LOCATION),
        ExifInterface.TAG_GPS_PROCESSING_METHOD to Pair("GPS Processing Method", MetadataCategory.LOCATION),
        
        // Technical Details
        ExifInterface.TAG_BITS_PER_SAMPLE to Pair("Bits Per Sample", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_COMPRESSION to Pair("Compression", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION to Pair("Photometric Interpretation", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_SAMPLES_PER_PIXEL to Pair("Samples Per Pixel", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_PLANAR_CONFIGURATION to Pair("Planar Configuration", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_Y_CB_CR_COEFFICIENTS to Pair("YCbCr Coefficients", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_Y_CB_CR_POSITIONING to Pair("YCbCr Positioning", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_X_RESOLUTION to Pair("X Resolution", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_Y_RESOLUTION to Pair("Y Resolution", MetadataCategory.TECHNICAL),
        ExifInterface.TAG_RESOLUTION_UNIT to Pair("Resolution Unit", MetadataCategory.TECHNICAL),
        
        // User Information
        ExifInterface.TAG_ARTIST to Pair("Artist", MetadataCategory.USER_DATA),
        ExifInterface.TAG_IMAGE_DESCRIPTION to Pair("Description", MetadataCategory.USER_DATA),
        ExifInterface.TAG_USER_COMMENT to Pair("User Comment", MetadataCategory.USER_DATA),
        ExifInterface.TAG_COPYRIGHT to Pair("Copyright", MetadataCategory.USER_DATA)
    )
    
    // Get tags by category for grouped display
    fun getTagsByCategory(category: MetadataCategory): Map<String, String> {
        return categorizedTags.filterValues { it.second == category }
            .mapValues { it.value.first }
    }
    
    // Get all categories that have tags
    fun getAllCategories(): List<MetadataCategory> {
        return categorizedTags.values.map { it.second }.distinct().sortedBy { it.ordinal }
    }
}