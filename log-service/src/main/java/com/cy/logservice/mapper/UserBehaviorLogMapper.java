package com.cy.logservice.mapper;

import com.cy.logservice.entity.UserBehaviorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBehaviorLogMapper {
    int insert(UserBehaviorLog log);

    List<UserBehaviorLog> listByUser(@Param("userId") Long userId);

    List<UserBehaviorLog> listByVideo(@Param("videoId") Long videoId);
}
