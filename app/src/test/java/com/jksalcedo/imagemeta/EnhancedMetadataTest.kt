package com.jksalcedo.imagemeta.ui.edit

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jksalcedo.imagemeta.ui.edit.MetadataCategory
import com.jksalcedo.imagemeta.ui.edit.EnhancedMetadata
import com.jksalcedo.imagemeta.ui.edit.MetadataFormat
import com.jksalcedo.imagemeta.ui.edit.EnhancedExifTags
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class EnhancedMetadataTest {

    @Test
    fun testMetadataCategories() {
        // Test that all metadata categories are properly defined
        val categories = MetadataCategory.values()
        assertTrue("Should have at least 6 categories", categories.size >= 6)
        
        val categoryNames = categories.map { it.displayName }
        assertTrue("Should contain Basic Information", categoryNames.contains("Basic Information"))
        assertTrue("Should contain Camera Settings", categoryNames.contains("Camera Settings"))
        assertTrue("Should contain Location Data", categoryNames.contains("Location Data"))
    }

    @Test
    fun testEnhancedMetadataCreation() {
        // Test creating enhanced metadata objects
        val metadata = EnhancedMetadata(
            tag = "TEST_TAG",
            label = "Test Label",
            value = "Test Value",
            category = MetadataCategory.BASIC_INFO,
            format = MetadataFormat.EXIF,
            isEditable = true
        )
        
        assertEquals("TEST_TAG", metadata.tag)
        assertEquals("Test Label", metadata.label)
        assertEquals("Test Value", metadata.value)
        assertEquals(MetadataCategory.BASIC_INFO, metadata.category)
        assertEquals(MetadataFormat.EXIF, metadata.format)
        assertTrue(metadata.isEditable)
    }

    @Test
    fun testEnhancedExifTagsCategorization() {
        // Test that ExifTags are properly categorized
        val allTags = EnhancedExifTags.categorizedTags
        assertTrue("Should have multiple tags", allTags.size > 20)
        
        // Test specific category assignments
        val makeTag = allTags["Make"]
        assertNotNull("Make tag should exist", makeTag)
        
        val categories = EnhancedExifTags.getAllCategories()
        assertTrue("Should have multiple categories", categories.size >= 5)
        assertTrue("Should contain BASIC_INFO", categories.contains(MetadataCategory.BASIC_INFO))
        assertTrue("Should contain CAMERA_SETTINGS", categories.contains(MetadataCategory.CAMERA_SETTINGS))
        assertTrue("Should contain LOCATION", categories.contains(MetadataCategory.LOCATION))
    }

    @Test
    fun testMetadataFormats() {
        // Test that all metadata formats are supported
        val formats = MetadataFormat.values()
        assertEquals("Should have exactly 3 formats", 3, formats.size)
        assertTrue("Should contain EXIF", formats.contains(MetadataFormat.EXIF))
        assertTrue("Should contain IPTC", formats.contains(MetadataFormat.IPTC))
        assertTrue("Should contain XMP", formats.contains(MetadataFormat.XMP))
    }
}