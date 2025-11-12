package com.cy.userservice.mapper;

import com.cy.userservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);

    User findByEmail(String email);

    int insert(User user);

    User findById(Long id);

    List<User> findAll();
}
