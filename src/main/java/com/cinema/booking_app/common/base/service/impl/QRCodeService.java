package com.cinema.booking_app.common.base.service.impl;

import com.cinema.booking_app.common.base.service.CloudinaryService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class QRCodeService {
    private final CloudinaryService cloudinaryService;

    public byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "JPG", jpgOutputStream);

            return jpgOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Could not generate QR Code", e);
        }
    }

    public String generateAndUploadQRCode(Long bookingCode) throws IOException {
        // 1. Sinh QR Code
        byte[] qrBytes = generateQRCodeImage(bookingCode.toString(), 300, 300);

        // 2. Convert byte[] -> MultipartFile
        MultipartFile multipartFile = new MockMultipartFile(
                "booking.jpg",
                "booking.jpg",
                "image/jpg",
                qrBytes
        );

        // 3. Upload lên Cloudinary
        return cloudinaryService.uploadImage(multipartFile);
    }

}
