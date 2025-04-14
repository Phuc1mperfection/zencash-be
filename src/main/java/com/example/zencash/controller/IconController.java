package com.example.zencash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/icons")
public class IconController {

    private static final String ICON_DIR = "src/main/resources/static/image/icon/";

    //Lấy ra list icon
    @GetMapping
    public ResponseEntity<List<String>> getIcons() {
        File folder = new File(ICON_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        String[] iconNames = folder.list((dir, name) ->
                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".svg"));
        if (iconNames == null) iconNames = new String[0];

        List<String> urls = Arrays.stream(iconNames)
                .map(name -> "/images/icons/" + name)
                .collect(Collectors.toList());

        return ResponseEntity.ok(urls);
    }
}

