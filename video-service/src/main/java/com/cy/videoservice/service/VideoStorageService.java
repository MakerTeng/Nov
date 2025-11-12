package com.cy.videoservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface VideoStorageService {

    String store(MultipartFile file);

    Resource load(String relativePath);

    void delete(String relativePath);

    String generateCover(String videoRelativePath);
}
