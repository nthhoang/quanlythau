package com.quanlythau.backend.service;

import com.quanlythau.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private static final long GIOI_HAN_BYTE = 10L * 1024 * 1024;

    public String luuFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File rỗng");
        }
        if (file.getSize() > GIOI_HAN_BYTE) {
            throw new BadRequestException("File vượt quá 10MB");
        }

        String tenGoc = file.getOriginalFilename() != null ? file.getOriginalFilename() : "anh.jpg";
        String phanMoRong = tenGoc.contains(".") ? tenGoc.substring(tenGoc.lastIndexOf('.')) : ".jpg";
        String tenFile = UUID.randomUUID() + phanMoRong;

        try {
            Path thuMuc = Path.of(uploadDir);
            Files.createDirectories(thuMuc);
            Path duongDan = thuMuc.resolve(tenFile);
            file.transferTo(duongDan);
        } catch (IOException e) {
            throw new BadRequestException("Lưu file thất bại: " + e.getMessage());
        }

        return "/uploads/" + tenFile;
    }
}
