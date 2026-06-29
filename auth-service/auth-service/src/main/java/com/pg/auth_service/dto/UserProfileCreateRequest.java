package com.pg.auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileCreateRequest {

    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
}
