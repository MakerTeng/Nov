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
        menus.add(new MenuItem("首页概览", "/dashboard"));
        menus.add(new MenuItem("视频播放", "/video/play"));

        if (UserRole.isAdmin(role)) {
            menus.add(new MenuItem("用户管理", "/users"));
            menus.add(new MenuItem("视频管理", "/videos"));
            menus.add(new MenuItem("视频上传", "/video/upload"));
            menus.add(new MenuItem("行为日志", "/logs"));
            menus.add(new MenuItem("推荐调试", "/recommend"));
        } else if (UserRole.CREATOR.getCode().equalsIgnoreCase(role)) {
            menus.add(new MenuItem("视频表现", "/videos"));
            menus.add(new MenuItem("推荐预览", "/recommend"));
            menus.add(new MenuItem("视频上传", "/video/upload"));
        } else {
            menus.add(new MenuItem("推荐视频", "/recommend"));
            menus.add(new MenuItem("上传视频", "/video/upload"));
        }

        menus.add(new MenuItem("个人中心", "/profile"));
        return menus;
    }
}
