package com.billio.billio_api.service;

import com.billio.billio_api.domain.Payment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Service for generating HUB 2D barcodes.
 */
@Service
public class Hub2BarcodeWriter implements PaymentBarcodeWriter {

    private final Logger log = LoggerFactory.getLogger(Hub2BarcodeWriter.class);

    @Override
    public void writeImage(Payment payment, OutputStream out) {
        var barcode = new Hub2DBarcode();
        barcode.setAmount(payment.getAmount());
        barcode.setCompanyName(payment.getRecipientName());
        if (payment.getRecipientAddress() != null) {
            barcode.setAddressStreet(payment.getRecipientAddress().getStreet());
            barcode.setAddressHouseNumber(payment.getRecipientAddress().getHouseNumber());
            if (payment.getRecipientAddress().getCity() != null) {
                barcode.setAddressPostalCode(payment.getRecipientAddress().getCity().getPostalCode());
                barcode.setAddressCity(payment.getRecipientAddress().getCity().getName());
            }
        }
        barcode.setIban(payment.getIban());
        barcode.setModel(payment.getModel());
        barcode.setCallNumber(payment.getCallNumber());
        barcode.setDescription(payment.getDescription());

        try {
            writeImage(barcode, out);
        } catch (IOException e) {
            throw new Hub2DBarcodeException("Failed to generate HUB 2D barcode due to IO exception.", e);
        } catch (WriterException e) {
            throw new Hub2DBarcodeException("Failed to generate HUB 2D barcode.", e);
        }
    }
    
    private void writeImage(Hub2DBarcode barcode, OutputStream out) throws IOException, WriterException {
        var hints = Collections.singletonMap(EncodeHintType.CHARACTER_SET, "UTF-8");
        var barcodeWriter = new PDF417Writer();
        log.debug("Generated HUB 2D barcode string: {}", barcode.toString());
        BitMatrix bitMatrix = barcodeWriter.encode(barcode.toString(), BarcodeFormat.PDF_417, 504, 236, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(bufferedImage, "PNG", out);
    }
}
