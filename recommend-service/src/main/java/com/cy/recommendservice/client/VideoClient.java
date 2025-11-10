package com.cy.recommendservice.client;

import com.cy.common.model.ApiResponse;
import com.cy.recommendservice.model.VideoInfo;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "video-service", contextId = "videoClient", path = "/api/video")
public interface VideoClient {

    @PostMapping("/batch")
    ApiResponse<List<VideoInfo>> batch(@RequestBody BatchRequest request);

    @GetMapping("/list")
    ApiResponse<List<VideoInfo>> list();

    class BatchRequest {
        @NotEmpty
        private List<Long> ids;

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }
    }
}
