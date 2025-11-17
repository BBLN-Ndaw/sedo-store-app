package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.IMAGE
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.model.dto.DeleteImagesRequest
import com.sedo.jwtauth.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * REST Controller for managing product image uploads and storage operations.
 *
 * This controller handles all image-related operations for products in the store
 * management system, including uploading multiple product images, storing them
 * securely, and managing image deletion. Images are essential for product
 * presentation and customer engagement in e-commerce operations.
 *
 * The controller supports multiple image formats and implements secure file
 * handling with proper validation and storage management.
 *
 * @property imageService Service layer for image processing and storage operations
 *
 */
@RestController
@RequestMapping(IMAGE)
class ImageController(
    private val imageService: ImageService
) {

    /**
     * Uploads multiple images for a specific product.
     *
     * This endpoint accepts multipart file uploads and associates them with
     * a specific product. Images are processed, validated, and stored securely
     * with generated URLs for future retrieval. Supports multiple image formats
     * including JPEG, PNG, and WebP.
     *
     * @param productName Name of the product to associate images with
     * @param files List of MultipartFile objects containing image data
     * @return ResponseEntity with HTTP 201 status containing list of generated image URLs
     * @throws InvalidImageFormatException if uploaded files are not valid images
     * @throws FileSizeExceededException if image files exceed size limits
     * @throws StorageException if image storage operation fails
     *
     * Security: Requires ADMIN or EMPLOYEE role for image upload operations
     */
    @PostMapping("/{productName}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun uploadProductImages(
        @PathVariable productName: String,
        @RequestParam("images") files: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        val imageUrls = imageService.uploadImages(files, productName)
        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrls)

    }

    /**
     * Deletes specified product images from storage.
     *
     * This endpoint removes one or more images from the storage system
     * based on the provided deletion request. It ensures proper cleanup
     * of both file storage and database references.
     *
     * @param deleteImagesRequest Request object containing list of image URLs or IDs to delete
     * @return ResponseEntity containing list of successfully deleted image identifiers
     * @throws ImageNotFoundException if specified images don't exist
     * @throws StorageException if image deletion operation fails
     *
     * Security: Requires ADMIN or EMPLOYEE role for image deletion operations
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteProductImage(
        @RequestBody deleteImagesRequest: DeleteImagesRequest
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(imageService.deleteImages(deleteImagesRequest))
    }
}