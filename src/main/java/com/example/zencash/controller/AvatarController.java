package com.example.zencash.controller;

import com.example.zencash.exception.AppException;
import com.example.zencash.utils.ErrorCode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    // Đường dẫn tuyệt đối đến folder ảnh (cùng cấp với src/)
    private static final String ICON_DIR = System.getProperty("user.dir") + "/image/avatar/";

    @GetMapping
    public ResponseEntity<?> getAllIcons() {
        File folder = new File(ICON_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Avatar directory not found.");
        }

        String[] icons = folder.list((dir, name) -> name.endsWith(".svg") || name.endsWith(".png") || name.endsWith(".jpg"));
        return ResponseEntity.ok(icons);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getIcon(@PathVariable String filename) {
        File file = new File(ICON_DIR + filename);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_EMPTY);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File targetFile = new File(ICON_DIR + filename);

        try {
            file.transferTo(targetFile);
            return ResponseEntity.ok(filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }
}
