package com.cy.userservice.service;

import com.cy.userservice.dto.UserLoginRequest;
import com.cy.userservice.dto.UserLoginResponse;
import com.cy.userservice.dto.UserProfileResponse;
import com.cy.userservice.dto.UserRegisterRequest;

import java.util.List;

public interface UserService {
    Long register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

    UserProfileResponse profile(Long userId);

    List<UserProfileResponse> listAll();
}
