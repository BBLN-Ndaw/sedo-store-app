package com.sedo.jwtauth.config

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for MinIO object storage integration.
 *
 * This configuration sets up the MinIO client for handling file storage operations,
 * particularly for product images in the store management system.
 * MinIO provides S3-compatible object storage for scalable file management.
 *
 */
@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioConfig {

    /**
     * Creates and configures a MinIO client bean.
     *
     * @param minioProperties Configuration properties for MinIO connection
     * @return Configured MinioClient instance
     */
    @Bean
    fun minioClient(minioProperties: MinioProperties): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.url)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()
    }
}

/**
 * Configuration properties for MinIO object storage.
 *
 * These properties define the connection parameters and credentials
 * for the MinIO storage server used for file uploads.
 *
 * @property url MinIO server endpoint URL
 * @property accessKey Access key for MinIO authentication
 * @property secretKey Secret key for MinIO authentication
 * @property bucketName Default bucket name for storing files
 */
@ConfigurationProperties(prefix = "app.minio")
data class MinioProperties(
    val url: String = "http://localhost:9000",
    val accessKey: String = "minioadmin",
    val secretKey: String = "minioadmin123",
    val bucketName: String = "product-images"
)