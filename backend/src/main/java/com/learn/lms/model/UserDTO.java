package com.learn.lms.model;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String email;
    private String role;
}