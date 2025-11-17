package com.sedo.jwtauth.service

import com.sedo.jwtauth.config.MinioProperties
import com.sedo.jwtauth.model.dto.DeleteImagesRequest
import io.minio.*
import io.minio.http.Method
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Service class responsible for image storage and management using MinIO object storage.
 * 
 * This service provides comprehensive image management functionality including:
 * - Image upload with validation and processing
 * - Batch image operations (upload/delete)
 * - Presigned URL generation for secure image access
 * - Image file validation (type, size, format)
 * - Organized storage structure by product names
 * 
 * Business Logic:
 * - Images are organized by product names in folder structure
 * - Supports common image formats (JPEG, JPG, PNG, GIF, WebP)
 * - Maximum file size limit of 5MB per image
 * - Generates unique filenames to prevent conflicts
 * - Provides secure access through presigned URLs
 * 
 * Storage Architecture:
 * - Uses MinIO for scalable object storage
 * - Bucket-based organization with automatic bucket creation
 * - Hierarchical folder structure: products/productname/imagename
 * - Presigned URLs for secure temporary access
 * 
 * Integration Points:
 * - Product management for image associations
 * - File upload handling from web interface
 * - Security through presigned URL generation
 * 
 * Dependencies:
 * - MinIO client for object storage operations
 * - MinioProperties for configuration management
 *
 */
@Service
class ImageService(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    init {
        createBucketIfNotExists()
    }

    /**
     * Creates the MinIO bucket if it doesn't exist.
     * Called during service initialization to ensure storage availability.
     */
    private fun createBucketIfNotExists() {
        try {
            val bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioProperties.bucketName)
                    .build()
            )
            
            if (!bucketExists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(minioProperties.bucketName)
                        .build()
                )
                logger.info("Bucket '${minioProperties.bucketName}' created successfully")
            }
        } catch (e: Exception) {
            logger.error("Error creating bucket: ${e.message}", e)
            throw RuntimeException("Failed to create MinIO bucket", e)
        }
    }

    /**
     * Uploads a single image file to MinIO storage.
     * 
     * Business Process:
     * 1. Validates image file format and size
     * 2. Generates unique filename with product association
     * 3. Uploads to organized folder structure
     * 4. Returns filename for database storage
     * 
     * Validation Rules:
     * - File must not be empty
     * - Must be valid image format (JPEG, JPG, PNG, GIF, WebP)
     * - Maximum file size of 5MB
     * 
     * @param file The MultipartFile containing image data
     * @param productName Product name for organizing storage structure
     * @return Generated filename for the uploaded image
     * @throws IllegalArgumentException if file validation fails
     * @throws RuntimeException if upload operation fails
     */
    fun uploadImage(file: MultipartFile, productName: String): String {
        try {
            validateImageFile(file)
            
            val fileName = generateFileName(file, productName)
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioProperties.bucketName)
                    .`object`(fileName)
                    .stream(file.inputStream, file.size, -1)
                    .contentType(file.contentType)
                    .build()
            )
            
            logger.info("Image uploaded successfully: $fileName")
            return fileName
            
        } catch (e: Exception) {
            logger.error("Error uploading image: ${e.message}", e)
            throw RuntimeException("Failed to upload image", e)
        }
    }

    /**
     * Uploads multiple image files in batch operation.
     * Convenient method for handling multiple product images.
     * 
     * @param files List of MultipartFile objects to upload
     * @param productName Product name for organizing storage structure
     * @return List of generated filenames for the uploaded images
     */
    fun uploadImages(files: List<MultipartFile>, productName: String): List<String> {
        return files.map { file -> uploadImage(file, productName) }
    }

    /**
     * Deletes a single image from MinIO storage.
     * 
     * @param imagesName The filename of the image to delete
     */
    fun deleteImage(imagesName: String) {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucketName)
                    .`object`(imagesName)
                    .build()
            )
            logger.info("Image deleted successfully: $imagesName")
    }

    /**
     * Deletes multiple images in batch operation.
     * Used when removing product images or cleaning up unused images.
     * 
     * @param deleteImagesRequest Request object containing list of image URLs to delete
     * @return List of successfully deleted image names
     */
    fun deleteImages(deleteImagesRequest: DeleteImagesRequest): List<String> {
        val deletedImages = mutableListOf<String>()
        deleteImagesRequest.imageUrls.forEach { imageName ->
            deleteImage(imageName)
            deletedImages.add(imageName)
        }
        return deletedImages
    }

    /**
     * Validates image file format, size, and content.
     * 
     * @param file The MultipartFile to validate
     * @throws IllegalArgumentException if validation fails
     */
    private fun validateImageFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw IllegalArgumentException("File is empty")
        }
        
        val allowedTypes = listOf("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp")
        if (file.contentType !in allowedTypes) {
            throw IllegalArgumentException("File type not supported. Allowed types: ${allowedTypes.joinToString()}")
        }
        
        val maxSize = 5 * 1024 * 1024 // 5MB
        if (file.size > maxSize) {
            throw IllegalArgumentException("File size exceeds maximum limit of 5MB")
        }
    }

    /**
     * Generates unique filename for uploaded images.
     * Creates organized folder structure with product association.
     * 
     * @param file The original file for extension extraction
     * @param productName Product name for folder organization
     * @return Generated unique filename with path
     */
    private fun generateFileName(file: MultipartFile, productName: String): String {
        val extension = file.originalFilename?.substringAfterLast('.') ?: "jpg"
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().take(8)
        val sanitizedOriginalFilename = file.originalFilename?.replace("\\s+".toRegex(), "_")?.replace(Regex("\\.[^.]+$"),"") ?: "image"
        val sanitizedProductName = productName.replace("\\s+".toRegex(), "_").lowercase()
        return "products/$sanitizedProductName/${sanitizedOriginalFilename}_${timestamp}_${uuid}.$extension"
    }

    /**
     * Generates presigned URL for secure image access.
     * Provides temporary access to images without exposing storage credentials.
     * 
     * @param fileName The filename of the image to access
     * @param expiryHours Number of hours before the URL expires (default: 24)
     * @return Presigned URL for secure image access
     * @throws RuntimeException if URL generation fails
     */
    fun generatePresignedUrl(fileName: String, expiryHours: Int = 24): String {
        return try {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.bucketName)
                    .`object`(fileName)
                    .expiry(expiryHours, TimeUnit.HOURS)
                    .build()
            )
        } catch (e: Exception) {
            logger.error("Error generating presigned URL for fileName: $fileName, error: ${e.message}", e)
            throw RuntimeException("Failed to generate presigned URL", e)
        }
    }

    /**
     * Generates presigned URLs for multiple images in batch operation.
     * 
     * @param fileNames List of filenames to generate URLs for
     * @param expiryHours Number of hours before URLs expire (default: 24)
     * @return List of presigned URLs corresponding to the filenames
     */
    fun generatePresignedUrls(fileNames: List<String>, expiryHours: Int = 24): List<String> {
        return fileNames.map { fileName -> generatePresignedUrl(fileName, expiryHours) }
    }
}