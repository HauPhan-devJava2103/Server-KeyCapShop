package com.vn.keycap_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Fetch avatarMedia cùng user để response login không truy cập quan hệ lazy sau khi repository đóng.
    @EntityGraph(attributePaths = "avatarMedia")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "avatarMedia")
    Optional<User> findProfileById(Long id);
}
