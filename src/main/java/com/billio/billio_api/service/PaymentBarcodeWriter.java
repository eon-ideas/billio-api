package com.billio.billio_api.service;

import com.billio.billio_api.domain.Payment;

import java.io.OutputStream;

/**
 * Interface for payment barcode writers.
 */
public interface PaymentBarcodeWriter {
    
    /**
     * Write a payment barcode image to the output stream.
     *
     * @param payment the payment information
     * @param out the output stream to write the image to
     */
    void writeImage(Payment payment, OutputStream out);
}
