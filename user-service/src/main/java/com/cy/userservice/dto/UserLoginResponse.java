package com.cy.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserLoginResponse {
    private String token;
    private UserProfileResponse profile;
    private List<MenuItem> menus;
}
