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

    List<Video> listPaged(@Param("includeInactive") boolean includeInactive,
                          @Param("offset") int offset,
                          @Param("size") int size);

    List<Video> listByUploader(Long uploaderId);

    int increaseLike(@Param("id") Long id);

    int increasePlay(@Param("id") Long id);

    int increasePlayByFile(@Param("fileUrl") String fileUrl);

    List<Video> findByIds(@Param("ids") List<Long> ids);

    int delete(@Param("id") Long id);
}
