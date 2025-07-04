package com.jksalcedo.imagemeta.utils

import androidx.exifinterface.media.ExifInterface

object GpsUtils {
    
    /**
     * Convert GPS coordinates from EXIF format to decimal degrees
     */
    fun parseGpsCoordinates(exif: ExifInterface): Pair<Double?, Double?> {
        val latValue = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val lngValue = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        
        val latitude = convertDmsToDecimal(latValue, latRef)
        val longitude = convertDmsToDecimal(lngValue, lngRef)
        
        return Pair(latitude, longitude)
    }
    
    /**
     * Convert decimal degrees to DMS format for EXIF
     */
    fun convertDecimalToDms(decimal: Double): String {
        val degrees = kotlin.math.abs(decimal).toInt()
        val minutesFloat = (kotlin.math.abs(decimal) - degrees) * 60
        val minutes = minutesFloat.toInt()
        val seconds = (minutesFloat - minutes) * 60
        
        return "$degrees/1,$minutes/1,${(seconds * 1000000).toInt()}/1000000"
    }
    
    /**
     * Convert DMS format from EXIF to decimal degrees
     */
    private fun convertDmsToDecimal(dmsValue: String?, ref: String?): Double? {
        if (dmsValue == null || ref == null) return null
        
        try {
            val parts = dmsValue.split(",")
            if (parts.size != 3) return null
            
            val degrees = parseFraction(parts[0])
            val minutes = parseFraction(parts[1])
            val seconds = parseFraction(parts[2])
            
            var decimal = degrees + (minutes / 60.0) + (seconds / 3600.0)
            
            if (ref == "S" || ref == "W") {
                decimal = -decimal
            }
            
            return decimal
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun parseFraction(fraction: String): Double {
        if (fraction.contains("/")) {
            val parts = fraction.split("/")
            return parts[0].toDouble() / parts[1].toDouble()
        }
        return fraction.toDouble()
    }
    
    /**
     * Get GPS reference (N/S for latitude, E/W for longitude)
     */
    fun getGpsReference(decimal: Double, isLatitude: Boolean): String {
        return if (isLatitude) {
            if (decimal >= 0) "N" else "S"
        } else {
            if (decimal >= 0) "E" else "W"
        }
    }
    
    /**
     * Validate GPS coordinates
     */
    fun isValidLatitude(latitude: Double): Boolean {
        return latitude >= -90.0 && latitude <= 90.0
    }
    
    fun isValidLongitude(longitude: Double): Boolean {
        return longitude >= -180.0 && longitude <= 180.0
    }
    
    /**
     * Format coordinates for display
     */
    fun formatCoordinates(latitude: Double?, longitude: Double?): String {
        return if (latitude != null && longitude != null) {
            val latDir = if (latitude >= 0) "N" else "S"
            val lngDir = if (longitude >= 0) "E" else "W"
            "${kotlin.math.abs(latitude).format(6)}° $latDir, ${kotlin.math.abs(longitude).format(6)}° $lngDir"
        } else {
            "No GPS coordinates"
        }
    }
    
    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
}