package com.pg.user.service.impl;

import com.pg.user.dto.CreateUserRequest;
import com.pg.user.dto.InternalUserRequest;
import com.pg.user.dto.PagedResponse;
import com.pg.user.dto.UpdateUserRequest;
import com.pg.user.dto.UserProfileResponse;
import com.pg.user.entity.UserProfile;
import com.pg.user.exception.AccessDeniedException;
import com.pg.user.exception.UserAlreadyExistsException;
import com.pg.user.exception.UserNotFoundException;
import com.pg.user.mapper.UserMapper;
import com.pg.user.repository.UserRepository;
import com.pg.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserProfileResponse createProfileInternal(InternalUserRequest request) {
        log.info("[Internal] Creating profile for authUserId: {}", request.getAuthUserId());

        if (userRepository.existsByAuthUserId(request.getAuthUserId())) {
            log.warn("[Internal] Profile already exists for authUserId: {}", request.getAuthUserId());
            return userMapper.toResponse(userRepository.findByAuthUserId(request.getAuthUserId()).get());
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(request.getAuthUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .build();

        return userMapper.toResponse(userRepository.save(profile));
    }

    @Override
    @Transactional
    public UserProfileResponse createProfile(CreateUserRequest request) {
        log.info("Creating user profile for authUserId: {}", request.getAuthUserId());

        if (userRepository.existsByAuthUserId(request.getAuthUserId())) {
            throw new UserAlreadyExistsException("Profile already exists for authUserId: " + request.getAuthUserId());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + request.getEmail());
        }
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new UserAlreadyExistsException("Mobile already in use: " + request.getMobile());
        }

        UserProfile saved = userRepository.save(userMapper.toEntity(request));
        log.info("User profile created with id: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileById(Long id) {
        log.debug("Fetching user profile by id: {}", id);
        return userMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByAuthUserId(Long authUserId) {
        log.debug("Fetching user profile by authUserId: {}", authUserId);
        UserProfile profile = userRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for authUserId: " + authUserId));
        return userMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByEmail(String email) {
        log.debug("Fetching user profile by email: {}", email);
        UserProfile profile = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for email: " + email));
        return userMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long id, UpdateUserRequest request, Long requestingAuthUserId) {
        log.info("Updating user profile id: {} by authUserId: {}", id, requestingAuthUserId);
        UserProfile profile = findById(id);
        verifyOwnership(profile, requestingAuthUserId);
        userMapper.updateEntity(profile, request);
        return userMapper.toResponse(userRepository.save(profile));
    }

    @Override
    @Transactional
    public void deleteProfile(Long id, Long requestingAuthUserId) {
        log.info("Deleting user profile id: {} by authUserId: {}", id, requestingAuthUserId);
        UserProfile profile = findById(id);
        verifyOwnership(profile, requestingAuthUserId);
        userRepository.delete(profile);
        log.info("User profile id: {} deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserProfileResponse> searchUsers(String name, String city, String occupation,
                                                           Boolean isVerified, Pageable pageable) {
        log.debug("Searching users - name: {}, city: {}, occupation: {}, verified: {}", name, city, occupation, isVerified);
        Page<UserProfile> page = userRepository.searchUsers(name, city, occupation, isVerified, pageable);
        List<UserProfileResponse> content = page.getContent().stream().map(userMapper::toResponse).toList();

        return PagedResponse.<UserProfileResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private UserProfile findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private void verifyOwnership(UserProfile profile, Long requestingAuthUserId) {
        if (!profile.getAuthUserId().equals(requestingAuthUserId)) {
            throw new AccessDeniedException("You are not allowed to modify this profile");
        }
    }
}
