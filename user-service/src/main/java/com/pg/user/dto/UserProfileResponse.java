package com.pg.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pg.user.entity.UserProfile.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private Long id;
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String mobile;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String profileImage;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String occupation;
    private String aadhaarNumber;
    private String panNumber;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
