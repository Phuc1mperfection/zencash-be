package com.example.zencash.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    // Đường dẫn tuyệt đối đến folder ảnh (cùng cấp với src/)
    private static final String ICON_DIR = System.getProperty("user.dir") + "/image/avatar/";

    @GetMapping("/image/avatar/{filename:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        File file = new File("src/main/resources/static/image/avatar/" + filename);
        if (!file.exists()) {
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
}
