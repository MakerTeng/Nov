package com.cy.userservice.service;

import com.cy.userservice.dto.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItem> buildMenus(String role);
}
