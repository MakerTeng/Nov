package com.cy.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** signing secret */
    private String secret = "demo-secret";

    /** expiration window in minutes */
    private long expireMinutes = 120;

    /** token issuer */
    private String issuer = "company-app";
}
