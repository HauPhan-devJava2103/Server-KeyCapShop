package com.vn.keycap_server.helpers;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.utils.ERole;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;

@Component
public class TestDataFactory {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createDummyUser() {
        return createCustomUser("user_" + UUID.randomUUID() + "@test.com", "TestUser", "0909000000", ERole.USER);
    }

    public User createDummyAdmin() {
        return createCustomUser("admin_" + UUID.randomUUID() + "@test.com", "TestAdmin", "0909111111", ERole.ADMIN);
    }

    public User createCustomUser(String email, String fullName, String phone, ERole role) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode("Password123!"))
                .fullName(fullName)
                .phone(phone)
                .role(role)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();
        return userRepository.save(user);
    }
}
