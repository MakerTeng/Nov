package com.cy.logservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogCollectRequest {

    @NotNull(message = "video id is required")
    private Long videoId;

    @NotBlank(message = "action is required")
    private String action;

    private String detail;
}
