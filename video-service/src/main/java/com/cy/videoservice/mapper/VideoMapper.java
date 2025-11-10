package com.cy.videoservice.mapper;

import com.cy.videoservice.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoMapper {
    int insert(Video video);

    Video findById(Long id);

    List<Video> listAll();

    List<Video> listActive();

    List<Video> listByUploader(Long uploaderId);

    int increaseLike(@Param("id") Long id);

    int increasePlay(@Param("id") Long id);

    List<Video> findByIds(@Param("ids") List<Long> ids);
}
