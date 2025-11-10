package com.cy.videoservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class VideoBatchRequest {

    @NotEmpty(message = "video id list must not be empty")
    private List<Long> ids;
}
