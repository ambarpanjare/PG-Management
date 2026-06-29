package com.pg.auth_service.client;

import com.pg.auth_service.dto.UserProfileCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/api/users/internal/profile")
    void createUserProfile(
            @RequestHeader("X-Internal-Token") String internalToken,
            @RequestBody UserProfileCreateRequest request
    );
}
