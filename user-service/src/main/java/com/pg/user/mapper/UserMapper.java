package com.pg.user.mapper;

import com.pg.user.dto.CreateUserRequest;
import com.pg.user.dto.UpdateUserRequest;
import com.pg.user.dto.UserProfileResponse;
import com.pg.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfile toEntity(CreateUserRequest request) {
        return UserProfile.builder()
                .authUserId(request.getAuthUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .profileImage(request.getProfileImage())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .pincode(request.getPincode())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactNumber(request.getEmergencyContactNumber())
                .occupation(request.getOccupation())
                .aadhaarNumber(request.getAadhaarNumber())
                .panNumber(request.getPanNumber())
                .build();
    }

    public UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .fullName(profile.getFirstName() + " " + profile.getLastName())
                .email(profile.getEmail())
                .mobile(profile.getMobile())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .profileImage(profile.getProfileImage())
                .address(profile.getAddress())
                .city(profile.getCity())
                .state(profile.getState())
                .country(profile.getCountry())
                .pincode(profile.getPincode())
                .emergencyContactName(profile.getEmergencyContactName())
                .emergencyContactNumber(profile.getEmergencyContactNumber())
                .occupation(profile.getOccupation())
                .aadhaarNumber(profile.getAadhaarNumber())
                .panNumber(profile.getPanNumber())
                .isVerified(profile.getIsVerified())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public void updateEntity(UserProfile profile, UpdateUserRequest request) {
        if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) profile.setLastName(request.getLastName());
        if (request.getMobile() != null) profile.setMobile(request.getMobile());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getProfileImage() != null) profile.setProfileImage(request.getProfileImage());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getPincode() != null) profile.setPincode(request.getPincode());
        if (request.getEmergencyContactName() != null) profile.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactNumber() != null) profile.setEmergencyContactNumber(request.getEmergencyContactNumber());
        if (request.getOccupation() != null) profile.setOccupation(request.getOccupation());
        if (request.getAadhaarNumber() != null) profile.setAadhaarNumber(request.getAadhaarNumber());
        if (request.getPanNumber() != null) profile.setPanNumber(request.getPanNumber());
    }
}
