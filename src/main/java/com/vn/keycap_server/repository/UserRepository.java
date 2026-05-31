package com.vn.keycap_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.keycap_server.modal.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find User By Email
    Optional<User> findByEmail(String email);
}
