package com.cy.videoservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoUploadRequest {

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotBlank(message = "file url is required")
    private String fileUrl;

    private String coverUrl;
}
