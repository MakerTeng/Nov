package com.cy.gateway.filter;

import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.JwtHelper;
import com.cy.common.security.LoginUser;
import com.cy.common.security.SecurityConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register",
            "/api/video/media/**",
            "/actuator/**"
    );

    private final JwtHelper jwtHelper;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isWhiteList(request.getPath().value())) {
            ServerHttpRequest forwardedRequest = ensureHeaders(exchange.getRequest());
            return chain.filter(exchange.mutate().request(forwardedRequest).build());
        }
        String token = resolveToken(request);
        if (token == null) {
            return writeResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "Missing token");
        }
        LoginUser loginUser;
        try {
            loginUser = jwtHelper.parseToken(token);
        } catch (Exception ex) {
            return writeResponse(exchange.getResponse(), HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        ServerHttpRequest mutated = request.mutate()
                .header(SecurityConstants.HEADER_USER_ID, String.valueOf(loginUser.getUserId()))
                .header(SecurityConstants.HEADER_USER_NAME, loginUser.getUsername())
                .header(SecurityConstants.HEADER_USER_ROLE, loginUser.getRole())
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private ServerHttpRequest ensureHeaders(ServerHttpRequest request) {
        if (request.getHeaders().containsKey(SecurityConstants.HEADER_USER_ID)) {
            return request;
        }
        return request.mutate()
                .header(SecurityConstants.HEADER_USER_ID, "0")
                .header(SecurityConstants.HEADER_USER_ROLE, "GUEST")
                .header(SecurityConstants.HEADER_USER_NAME, "guest")
                .build();
    }

    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String resolveToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(SecurityConstants.HEADER_TOKEN);
        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ApiResponse<Void> body = ApiResponse.failure(ErrorCode.UNAUTHORIZED.getCode(), message);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":401,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
