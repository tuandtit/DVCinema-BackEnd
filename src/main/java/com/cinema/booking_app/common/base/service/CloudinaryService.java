package com.cinema.booking_app.common.base.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;

    void deleteFile(String url);
}
