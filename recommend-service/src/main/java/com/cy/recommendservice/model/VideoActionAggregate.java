package com.cy.recommendservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoActionAggregate {
    private Long videoId;
    private Double watchScore;
    private Double likedScore;
    private Double commentedScore;
    private LocalDateTime lastActionTime;
}
