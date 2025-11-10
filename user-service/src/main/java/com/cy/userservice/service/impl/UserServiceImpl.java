package com.cy.userservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.security.JwtHelper;
import com.cy.common.security.LoginUser;
import com.cy.userservice.dto.UserLoginRequest;
import com.cy.userservice.dto.UserLoginResponse;
import com.cy.userservice.dto.UserProfileResponse;
import com.cy.userservice.dto.UserRegisterRequest;
import com.cy.userservice.entity.User;
import com.cy.userservice.enums.UserRole;
import com.cy.userservice.mapper.UserMapper;
import com.cy.userservice.service.MenuService;
import com.cy.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MenuService menuService;
    private final JwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterRequest request) {
        User existing = userMapper.findByUsername(request.getUsername());
        if (existing != null) {
            throw new BizException(ErrorCode.USERNAME_EXISTS, "username already exists");
        }
        String requestRole = request.getRole() == null
                ? UserRole.USER.getCode()
                : request.getRole().toUpperCase(Locale.ROOT);
        if (!UserRole.contains(requestRole)) {
            throw new BizException(ErrorCode.VALIDATION_ERROR, "invalid role");
        }
        if (UserRole.isAdmin(requestRole)) {
            throw new BizException(ErrorCode.FORBIDDEN, "admin accounts must be created by existing admins");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(requestRole);
        user.setStatus(1);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.BAD_CREDENTIALS, "username or password is incorrect");
        }
        if (!Objects.equals(user.getStatus(), 1)) {
            throw new BizException(ErrorCode.FORBIDDEN, "account is disabled");
        }
        LoginUser loginUser = LoginUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        String token = jwtHelper.generateToken(loginUser);
        UserProfileResponse profile = toProfile(user);
        return UserLoginResponse.builder()
                .token(token)
                .profile(profile)
                .menus(menuService.buildMenus(user.getRole()))
                .build();
    }

    @Override
    public UserProfileResponse profile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND, "user not found");
        }
        return toProfile(user);
    }

    @Override
    public List<UserProfileResponse> listAll() {
        return userMapper.findAll()
                .stream()
                .map(this::toProfile)
                .collect(Collectors.toList());
    }

    private UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
