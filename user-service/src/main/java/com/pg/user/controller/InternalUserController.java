package com.pg.user.controller;

import com.pg.user.dto.InternalUserRequest;
import com.pg.user.dto.UserProfileResponse;
import com.pg.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users/internal")
public class InternalUserController {

    private final UserService userService;
    private final String internalServiceToken;

    public InternalUserController(UserService userService,
                                   @Value("${internal.service-token}") String internalServiceToken) {
        this.userService = userService;
        this.internalServiceToken = internalServiceToken;
    }

    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponse> createProfile(
            @RequestHeader("X-Internal-Token") String token,
            @RequestBody InternalUserRequest request) {

        if (!internalServiceToken.equals(token)) {
            log.warn("Unauthorized internal call attempt for authUserId: {}", request.getAuthUserId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("Internal profile creation request for authUserId: {}", request.getAuthUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createProfileInternal(request));
    }
}
