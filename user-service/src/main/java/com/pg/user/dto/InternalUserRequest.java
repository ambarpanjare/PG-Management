package com.pg.user.dto;

import lombok.Data;

@Data
public class InternalUserRequest {

    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
}
