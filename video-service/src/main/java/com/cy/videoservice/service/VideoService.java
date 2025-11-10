package com.cy.videoservice.service;

import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;

import java.util.List;

public interface VideoService {

    Long upload(Long userId, String username, VideoUploadRequest request);

    VideoResponse findById(Long id, boolean increasePlay);

    List<VideoResponse> listAll(boolean includeInactive);

    List<VideoResponse> listMine(Long userId);

    void like(Long id);

    List<VideoResponse> findByIds(List<Long> ids);
}
