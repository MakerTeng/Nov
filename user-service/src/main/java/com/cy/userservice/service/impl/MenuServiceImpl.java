package com.cy.userservice.service.impl;

import com.cy.userservice.dto.MenuItem;
import com.cy.userservice.enums.UserRole;
import com.cy.userservice.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Override
    public List<MenuItem> buildMenus(String role) {
        List<MenuItem> menus = new ArrayList<>();
        menus.add(new MenuItem("Discover", "/recommend"));
        menus.add(new MenuItem("My Profile", "/profile"));
        if (UserRole.CREATOR.getCode().equalsIgnoreCase(role) || UserRole.ADMIN.getCode().equalsIgnoreCase(role)) {
            menus.add(new MenuItem("Upload Video", "/video/upload"));
        }
        if (UserRole.isAdmin(role)) {
            menus.add(new MenuItem("User Management", "/admin/users"));
            menus.add(new MenuItem("Content Management", "/admin/videos"));
            menus.add(new MenuItem("Analytics", "/admin/stats"));
        }
        return menus;
    }
}
