package com.cy.logservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoActionRequest {

    @NotNull(message = "视频 ID 必填")
    private Long videoId;

    @Min(value = 0, message = "观看时长需大于等于 0")
    private Integer watchTime = 0;

    private Boolean liked = false;

    private Boolean commented = false;
}
