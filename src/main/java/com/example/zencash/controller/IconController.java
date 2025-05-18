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
@RequestMapping("/api/icons")
public class IconController {

    // Đường dẫn tuyệt đối đến folder ảnh (cùng cấp với src/)
    private static final String ICON_DIR = System.getProperty("user.dir") + "/image/icon/";

    @GetMapping
    public ResponseEntity<?> getAllIcons() {
        File folder = new File(ICON_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Icon directory not found.");
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
}
