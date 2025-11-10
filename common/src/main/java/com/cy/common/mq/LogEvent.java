package com.cy.common.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent {
    private Long userId;
    private Long videoId;
    private String action;
    private String detail;
    private Long timestamp;
}
