package com.eyecommer.Backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    // Phương thức tải ảnh lên Cloudinary
    public String uploadImage(MultipartFile file) {
        try {
            // Lệnh tải lên
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap() // Có thể thêm các tham số như folder, public_id ở đây
            );

            // Lấy URL an toàn (HTTPS) của ảnh đã tải lên
            String imageUrl = (String) uploadResult.get("secure_url");

            return imageUrl;

        } catch (IOException e) {
            // Xử lý ngoại lệ khi không thể đọc file
            // Hoặc có lỗi khi giao tiếp với Cloudinary
            throw new RuntimeException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
        }
    }
}
