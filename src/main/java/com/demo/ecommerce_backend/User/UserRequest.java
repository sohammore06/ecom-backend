package com.demo.ecommerce_backend.User;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String password;
    private String role;
    private String address;
    private Boolean active;
    private LocalDate dateOfBirth;
}
