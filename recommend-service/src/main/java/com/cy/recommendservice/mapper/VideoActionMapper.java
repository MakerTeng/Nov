package com.cy.recommendservice.mapper;

import com.cy.recommendservice.model.VideoActionAggregate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoActionMapper {

    List<VideoActionAggregate> aggregateByUser(@Param("userId") Long userId,
                                               @Param("limit") int limit,
                                               @Param("watchCap") int watchCap);
}
