package com.vn.keycap_server.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;
import com.vn.keycap_server.utils.ERole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Kiểm tra xem đã có tài khoản admin chưa
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin"))
                        .fullName("Administrator")
                        .role(ERole.ADMIN)
                        .build();

                userRepository.save(admin);
                log.warn("Admin account created: email=admin@gmail.com, password=admin. Please change the password!");
            } else {
                log.info("Admin account already exists, skipping initialization.");
            }
        };
    }

}
