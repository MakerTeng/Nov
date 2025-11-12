package com.cy.logservice.mapper;

import com.cy.logservice.entity.VideoAction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoActionMapper {

    int insert(VideoAction action);
}
