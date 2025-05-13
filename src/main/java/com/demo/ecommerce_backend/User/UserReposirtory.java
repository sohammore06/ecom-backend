package com.demo.ecommerce_backend.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReposirtory extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNumber);
}
