package com.pg.user.controller;

import com.pg.user.dto.ApiResponse;
import com.pg.user.dto.CreateUserRequest;
import com.pg.user.dto.PagedResponse;
import com.pg.user.dto.UpdateUserRequest;
import com.pg.user.dto.UserProfileResponse;
import com.pg.user.service.UserService;
import com.pg.user.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "User profile management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @Operation(summary = "Create user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createProfile(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/users - creating profile for authUserId: {}", request.getAuthUserId());
        UserProfileResponse response = userService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User profile created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfileById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", userService.getProfileById(id)));
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged-in user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        log.info("GET /api/users/me - email: {}", email);
        return ResponseEntity.ok(ApiResponse.success("Your profile fetched", userService.getProfileByEmail(email)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String authHeader) {
        Long authUserId = extractAuthUserId(authHeader);
        log.info("PUT /api/users/{} by authUserId: {}", id, authUserId);
        return ResponseEntity.ok(ApiResponse.success("User profile updated",
                userService.updateProfile(id, request, authUserId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long authUserId = extractAuthUserId(authHeader);
        log.info("DELETE /api/users/{} by authUserId: {}", id, authUserId);
        userService.deleteProfile(id, authUserId);
        return ResponseEntity.ok(ApiResponse.success("User profile deleted"));
    }

    @GetMapping
    @Operation(summary = "Search and list users with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileResponse>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<UserProfileResponse> result = userService.searchUsers(name, city, occupation, isVerified, pageable);
        return ResponseEntity.ok(ApiResponse.success("Users fetched", result));
    }

    private Long extractAuthUserId(String authHeader) {
        return jwtUtil.extractAuthUserId(authHeader.substring(7));
    }
}
