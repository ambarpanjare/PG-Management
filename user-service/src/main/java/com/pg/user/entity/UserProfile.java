package com.pg.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long authUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    private String profileImage;

    private String address;

    private String city;

    private String state;

    private String country;

    @Column(length = 10)
    private String pincode;

    private String emergencyContactName;

    private String emergencyContactNumber;

    private String occupation;

    @Column(length = 12)
    private String aadhaarNumber;

    @Column(length = 10)
    private String panNumber;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
