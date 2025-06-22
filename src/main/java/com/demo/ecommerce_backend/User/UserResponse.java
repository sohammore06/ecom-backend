package com.demo.ecommerce_backend.User;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String role;
    private String address;
    private Boolean active;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDateTime lastLogin;
}
