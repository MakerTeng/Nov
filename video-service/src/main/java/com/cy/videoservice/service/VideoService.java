package com.cy.videoservice.service;

import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;

import java.util.List;

public interface VideoService {

    Long upload(Long userId, String username, VideoUploadRequest request);

    VideoResponse findById(Long id, boolean increasePlay);

    List<VideoResponse> listAll(boolean includeInactive, int page, int size);

    List<VideoResponse> listMine(Long userId);

    void like(Long id);

    List<VideoResponse> findByIds(List<Long> ids);

    void delete(Long id, Long operatorId, boolean operatorIsAdmin);

    void recordPlayback(String relativePath);
}
