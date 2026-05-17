package com.recommend.server.controller;

import com.recommend.server.service.ImageStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StorageController {

    private final ImageStorageService imageStorageService;

    public StorageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/storage/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        ImageStorageService.ImageData imageData = imageStorageService.getImage(id);

        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = imageData.contentType() != null
                ? MediaType.parseMediaType(imageData.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageData.bytes());
    }

}
