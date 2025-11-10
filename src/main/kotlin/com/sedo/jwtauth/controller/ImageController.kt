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

@RestController
@RequestMapping(IMAGE)
class ImageController(
    private val imageService: ImageService
) {

    @PostMapping("/{productName}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun uploadProductImages(
        @PathVariable productName: String,
        @RequestParam("images") files: List<MultipartFile>
    ): ResponseEntity<List<String>> {
        val imageUrls = imageService.uploadImages(files, productName)
        return ResponseEntity.status(HttpStatus.CREATED).body(imageUrls)

    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteProductImage(
        @RequestBody deleteImagesRequest: DeleteImagesRequest
    ): ResponseEntity<List<String>> {
        return ResponseEntity.ok(imageService.deleteImages(deleteImagesRequest))
    }
}