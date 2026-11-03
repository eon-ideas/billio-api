package com.billio.billio_api.web.rest;

import com.billio.billio_api.domain.Payment;
import com.billio.billio_api.service.Hub2BarcodeWriter;
import com.billio.billio_api.service.PaymentBarcodeWriter;
import com.billio.billio_api.web.rest.errors.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for generating 2D payment barcode images for invoices.
 * CORS is handled globally in SecurityConfig.
 */
@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoice Barcode", description = "Invoice barcode generation API")
public class InvoiceBarcodeResource {

    private final Logger log = LoggerFactory.getLogger(InvoiceBarcodeResource.class);
    private final PaymentBarcodeWriter barcodeWriter;
    // Simple cache to store generated barcodes
    private final Map<UUID, byte[]> barcodeCache = new HashMap<>();

    public InvoiceBarcodeResource(Hub2BarcodeWriter barcodeWriter) {
        this.barcodeWriter = barcodeWriter;
    }

    /**
     * POST /invoices/{id}/2d-barcode.png : Generate a 2D payment barcode image (PDF417) for a given invoice
     *
     * @param id the UUID of the invoice
     * @param paymentRequest the payment details for barcode generation
     * @return the ResponseEntity with status 200 (OK) and the image as body, or with status 404 (Not Found)
     */
    @Operation(
        summary = "Generate a 2D payment barcode (PDF417) for an invoice",
        description = "Generates a 2D payment barcode image in PNG format for the specified invoice using the provided payment details",
        security = @SecurityRequirement(name = "Bearer Authentication"),
        tags = { "Invoice Barcode" }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Barcode generated successfully",
            content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - invalid or missing authentication token",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during barcode generation",
            content = @Content
        )
    })
    @PostMapping(value = "/{id}/2d-barcode.png", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> generateBarcode(
            @Parameter(description = "UUID of the invoice", required = true)
            @PathVariable UUID id,
            
            @Parameter(description = "Payment details for barcode generation", required = true)
            @Valid @RequestBody Payment paymentRequest) {
        
        log.debug("REST request to generate 2D barcode for Invoice : {}", id);
        
        try {
            // Create a byte array output stream to hold the barcode image
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            
            // Generate the barcode
            barcodeWriter.writeImage(paymentRequest, outputStream);
            
            // Cache the barcode for later retrieval
            byte[] barcodeImage = outputStream.toByteArray();
            barcodeCache.put(id, barcodeImage);
            
            // Return the image as a byte array
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(barcodeImage);
                
        } catch (Exception e) {
            log.error("Error generating barcode for invoice {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /invoices/{id}/2d-barcode.png : Get a previously generated 2D payment barcode image
     *
     * @param id the UUID of the invoice
     * @return the ResponseEntity with status 200 (OK) and the image as body, or with status 404 (Not Found)
     */
    @Operation(
        summary = "Get a previously generated 2D payment barcode",
        description = "Retrieves a previously generated 2D payment barcode image in PNG format for the specified invoice",
        tags = { "Invoice Barcode" }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Barcode retrieved successfully",
            content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary"))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Barcode not found for the specified invoice",
            content = @Content
        )
    })
    @GetMapping(value = "/{id}/2d-barcode.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcode(
            @Parameter(description = "UUID of the invoice", required = true)
            @PathVariable UUID id) {
        
        log.debug("REST request to get 2D barcode for Invoice : {}", id);
        
        // Check if we have a cached barcode for this invoice
        if (barcodeCache.containsKey(id)) {
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(barcodeCache.get(id));
        } else {
            throw new ResourceNotFoundException("Barcode not found for invoice: " + id);
        }
    }
}