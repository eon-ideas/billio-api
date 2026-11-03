package com.billio.billio_api.service;

/**
 * Exception thrown when there is an error generating a HUB 2D barcode.
 */
public class Hub2DBarcodeException extends RuntimeException {
    
    public Hub2DBarcodeException(String message) {
        super(message);
    }
    
    public Hub2DBarcodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
