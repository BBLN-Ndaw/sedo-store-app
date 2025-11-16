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

@Service
class ImageService(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    init {
        createBucketIfNotExists()
    }

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

    fun uploadImages(files: List<MultipartFile>, productName: String): List<String> {
        return files.map { file -> uploadImage(file, productName) }
    }

    fun deleteImage(imagesName: String) {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucketName)
                    .`object`(imagesName)
                    .build()
            )
            logger.info("Image deleted successfully: $imagesName")
    }

    fun deleteImages(deleteImagesRequest: DeleteImagesRequest): List<String> {
        val deletedImages = mutableListOf<String>()
        deleteImagesRequest.imageUrls.forEach { imageName ->
            deleteImage(imageName)
            deletedImages.add(imageName)
        }
        return deletedImages
    }

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

    private fun generateFileName(file: MultipartFile, productName: String): String {
        val extension = file.originalFilename?.substringAfterLast('.') ?: "jpg"
        val timestamp = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().take(8)
        val sanitizedOriginalFilename = file.originalFilename?.replace("\\s+".toRegex(), "_")?.replace(Regex("\\.[^.]+$"),"") ?: "image"
        val sanitizedProductName = productName.replace("\\s+".toRegex(), "_").lowercase()
        return "products/$sanitizedProductName/${sanitizedOriginalFilename}_${timestamp}_${uuid}.$extension"
    }

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

    fun generatePresignedUrls(fileNames: List<String>, expiryHours: Int = 24): List<String> {
        return fileNames.map { fileName -> generatePresignedUrl(fileName, expiryHours) }
    }
}