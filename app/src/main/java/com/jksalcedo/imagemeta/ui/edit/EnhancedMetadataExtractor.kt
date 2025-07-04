package com.jksalcedo.imagemeta.ui.edit

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.exifinterface.media.ExifInterface
import com.drew.metadata.MetadataException
import com.drew.metadata.iptc.IptcDirectory
import com.drew.metadata.xmp.XmpDirectory
import java.io.IOException
import java.io.InputStream

class EnhancedMetadataExtractor(private val context: Context) {

    fun extractAllMetadata(imageUri: Uri): List<EnhancedMetadata> {
        val metadataList = mutableListOf<EnhancedMetadata>()
        
        try {
            // Extract EXIF metadata
            metadataList.addAll(extractExifMetadata(imageUri))
            
            // Extract IPTC and XMP metadata using metadata-extractor library
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                metadataList.addAll(extractExtendedMetadata(inputStream))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return metadataList.sortedBy { it.category.ordinal }
    }
    
    private fun extractExifMetadata(imageUri: Uri): List<EnhancedMetadata> {
        val exifData = mutableListOf<EnhancedMetadata>()
        
        try {
            val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(imageUri, "r")
            pfd?.use { descriptor ->
                val exif = ExifInterface(descriptor.fileDescriptor)
                
                // Extract categorized EXIF data
                for ((tag, info) in EnhancedExifTags.categorizedTags) {
                    val value = exif.getAttribute(tag) ?: ""
                    exifData.add(
                        EnhancedMetadata(
                            tag = tag,
                            label = info.first,
                            value = value,
                            category = info.second,
                            format = MetadataFormat.EXIF
                        )
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        return exifData
    }
    
    private fun extractExtendedMetadata(inputStream: InputStream): List<EnhancedMetadata> {
        val extendedData = mutableListOf<EnhancedMetadata>()
        
        try {
            val metadata = com.drew.imaging.ImageMetadataReader.readMetadata(inputStream)
            
            // Extract IPTC metadata
            val iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory::class.java)
            iptcDirectory?.let { dir ->
                extendedData.addAll(extractIptcData(dir))
            }
            
            // Extract XMP metadata
            val xmpDirectory = metadata.getFirstDirectoryOfType(XmpDirectory::class.java)
            xmpDirectory?.let { dir ->
                extendedData.addAll(extractXmpData(dir))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return extendedData
    }
    
    private fun extractIptcData(directory: IptcDirectory): List<EnhancedMetadata> {
        val iptcData = mutableListOf<EnhancedMetadata>()
        
        try {
            // Common IPTC tags
            val iptcTags = mapOf(
                IptcDirectory.TAG_HEADLINE to Pair("Headline", MetadataCategory.USER_DATA),
                IptcDirectory.TAG_CAPTION to Pair("Caption", MetadataCategory.USER_DATA),
                IptcDirectory.TAG_KEYWORDS to Pair("Keywords", MetadataCategory.USER_DATA),
                IptcDirectory.TAG_CITY to Pair("City", MetadataCategory.LOCATION),
                IptcDirectory.TAG_PROVINCE_OR_STATE to Pair("State/Province", MetadataCategory.LOCATION),
                IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION to Pair("Country", MetadataCategory.LOCATION),
                IptcDirectory.TAG_BYLINE to Pair("Byline", MetadataCategory.USER_DATA),
                IptcDirectory.TAG_COPYRIGHT_NOTICE to Pair("Copyright", MetadataCategory.USER_DATA)
            )
            
            for ((tag, info) in iptcTags) {
                if (directory.hasTagName(tag)) {
                    val value = directory.getString(tag) ?: ""
                    iptcData.add(
                        EnhancedMetadata(
                            tag = "IPTC_$tag",
                            label = info.first,
                            value = value,
                            category = info.second,
                            format = MetadataFormat.IPTC
                        )
                    )
                }
            }
        } catch (e: MetadataException) {
            e.printStackTrace()
        }
        
        return iptcData
    }
    
    private fun extractXmpData(directory: XmpDirectory): List<EnhancedMetadata> {
        val xmpData = mutableListOf<EnhancedMetadata>()
        
        try {
            // Basic XMP metadata extraction
            val xmpMeta = directory.xmpMeta
            
            // Extract common XMP properties
            val xmpProperties = listOf(
                "dc:title" to Pair("Title", MetadataCategory.USER_DATA),
                "dc:description" to Pair("Description", MetadataCategory.USER_DATA),
                "dc:creator" to Pair("Creator", MetadataCategory.USER_DATA),
                "dc:subject" to Pair("Subject", MetadataCategory.USER_DATA),
                "xmp:Rating" to Pair("Rating", MetadataCategory.USER_DATA)
            )
            
            for ((property, info) in xmpProperties) {
                try {
                    val namespace = if (property.startsWith("dc:")) "http://purl.org/dc/elements/1.1/" 
                                   else "http://ns.adobe.com/xap/1.0/"
                    val localName = property.substringAfter(":")
                    
                    if (xmpMeta.doesPropertyExist(namespace, localName)) {
                        val value = xmpMeta.getPropertyString(namespace, localName) ?: ""
                        xmpData.add(
                            EnhancedMetadata(
                                tag = "XMP_$property",
                                label = info.first,
                                value = value,
                                category = info.second,
                                format = MetadataFormat.XMP
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Continue with other properties if one fails
                    continue
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return xmpData
    }
}