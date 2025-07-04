package com.jksalcedo.imagemeta.utils

import com.jksalcedo.imagemeta.utils.GpsUtils
import org.junit.Test
import org.junit.Assert.*

class GpsUtilsTest {

    @Test
    fun testCoordinateValidation() {
        // Test latitude validation
        assertTrue("Valid latitude should pass", GpsUtils.isValidLatitude(45.0))
        assertTrue("Valid negative latitude should pass", GpsUtils.isValidLatitude(-45.0))
        assertTrue("Boundary latitude should pass", GpsUtils.isValidLatitude(90.0))
        assertTrue("Boundary negative latitude should pass", GpsUtils.isValidLatitude(-90.0))
        assertFalse("Invalid high latitude should fail", GpsUtils.isValidLatitude(91.0))
        assertFalse("Invalid low latitude should fail", GpsUtils.isValidLatitude(-91.0))

        // Test longitude validation
        assertTrue("Valid longitude should pass", GpsUtils.isValidLongitude(120.0))
        assertTrue("Valid negative longitude should pass", GpsUtils.isValidLongitude(-120.0))
        assertTrue("Boundary longitude should pass", GpsUtils.isValidLongitude(180.0))
        assertTrue("Boundary negative longitude should pass", GpsUtils.isValidLongitude(-180.0))
        assertFalse("Invalid high longitude should fail", GpsUtils.isValidLongitude(181.0))
        assertFalse("Invalid low longitude should fail", GpsUtils.isValidLongitude(-181.0))
    }

    @Test
    fun testGpsReferenceGeneration() {
        // Test latitude references
        assertEquals("Positive latitude should be N", "N", GpsUtils.getGpsReference(45.0, true))
        assertEquals("Negative latitude should be S", "S", GpsUtils.getGpsReference(-45.0, true))
        assertEquals("Zero latitude should be N", "N", GpsUtils.getGpsReference(0.0, true))

        // Test longitude references
        assertEquals("Positive longitude should be E", "E", GpsUtils.getGpsReference(120.0, false))
        assertEquals("Negative longitude should be W", "W", GpsUtils.getGpsReference(-120.0, false))
        assertEquals("Zero longitude should be E", "E", GpsUtils.getGpsReference(0.0, false))
    }

    @Test
    fun testDecimalToDmsConversion() {
        // Test positive coordinate conversion
        val dms1 = GpsUtils.convertDecimalToDms(37.7749)
        assertNotNull("DMS conversion should not be null", dms1)
        assertTrue("DMS should contain degrees", dms1.contains("37"))

        // Test negative coordinate conversion
        val dms2 = GpsUtils.convertDecimalToDms(-122.4194)
        assertNotNull("DMS conversion should not be null", dms2)
        assertTrue("DMS should contain degrees", dms2.contains("122"))

        // Test zero conversion
        val dms3 = GpsUtils.convertDecimalToDms(0.0)
        assertNotNull("Zero DMS conversion should not be null", dms3)
        assertTrue("Zero DMS should start with 0", dms3.startsWith("0"))
    }

    @Test
    fun testCoordinateFormatting() {
        // Test valid coordinates formatting
        val formatted1 = GpsUtils.formatCoordinates(37.7749, -122.4194)
        assertTrue("Should contain latitude", formatted1.contains("37."))
        assertTrue("Should contain longitude", formatted1.contains("122."))
        assertTrue("Should contain N for positive latitude", formatted1.contains("N"))
        assertTrue("Should contain W for negative longitude", formatted1.contains("W"))

        // Test null coordinates
        val formatted2 = GpsUtils.formatCoordinates(null, null)
        assertEquals("Should return no GPS message", "No GPS coordinates", formatted2)

        // Test partial null coordinates
        val formatted3 = GpsUtils.formatCoordinates(37.7749, null)
        assertEquals("Should return no GPS message for partial null", "No GPS coordinates", formatted3)
    }
}