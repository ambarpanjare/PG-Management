package com.pg.user.service;

import com.pg.user.dto.CreateUserRequest;
import com.pg.user.dto.InternalUserRequest;
import com.pg.user.dto.PagedResponse;
import com.pg.user.dto.UpdateUserRequest;
import com.pg.user.dto.UserProfileResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserProfileResponse createProfile(CreateUserRequest request);

    UserProfileResponse createProfileInternal(InternalUserRequest request);

    UserProfileResponse getProfileById(Long id);

    UserProfileResponse getProfileByAuthUserId(Long authUserId);

    UserProfileResponse getProfileByEmail(String email);

    UserProfileResponse updateProfile(Long id, UpdateUserRequest request, Long requestingAuthUserId);

    void deleteProfile(Long id, Long requestingAuthUserId);

    PagedResponse<UserProfileResponse> searchUsers(String name, String city, String occupation,
                                                    Boolean isVerified, Pageable pageable);
}
