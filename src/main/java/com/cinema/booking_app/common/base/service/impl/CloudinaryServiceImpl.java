package com.cinema.booking_app.common.base.service.impl;

import com.cinema.booking_app.common.base.service.CloudinaryService;
import com.cinema.booking_app.common.error.BusinessException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null) {
            throw new BusinessException("400", "File name cannot be null");
        }
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileName(file.getOriginalFilename())[1];
        File fileUpload = convert(file);

        String fullPublicId = "dvcinema/" + publicValue;

        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", fullPublicId));
        cleanDisk(fileUpload);

        return cloudinary.url().generate(StringUtils.join(fullPublicId, ".", extension));

    }

    @Override
    public void deleteFile(String url) {
        try {
            // Tách public_id từ URL
            String publicId = extractPublicIdFromUrl(url);

            log.info("xóa publicId: {}", publicId);
            // Gọi Cloudinary để xóa file
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", "image"
            ));


            if (!"ok".equals(result.get("result"))) {
                throw new BusinessException("500", "Không thể xóa ảnh trên Cloudinary: " + result.get("result"));
            }

        } catch (IOException e) {
            throw new BusinessException("500", "Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        // Tìm vị trí bắt đầu từ "/upload/"
        int index = url.indexOf("/upload/");
        if (index == -1) {
            throw new BusinessException("400", "URL không hợp lệ: " + url);
        }
        String path = url.substring(index + "/upload/".length());

        // Nếu có version v1/ → loại bỏ
        if (path.matches("v\\d+/.*")) {
            path = path.substring(path.indexOf("/") + 1); // bỏ "v1/"
        }

        // Xóa phần mở rộng .jpg, .png,...
        return path.replaceAll("\\.[a-zA-Z0-9]+$", "");
    }

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        File convFile = new File(StringUtils.join(generatePublicValue(file.getOriginalFilename()), getFileName(file.getOriginalFilename())[1]));
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private void cleanDisk(File file) {
        try {
            log.info("file.toPath(): {}", file.toPath());
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error");
        }
    }

    public String generatePublicValue(String originalName) {
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }
}
