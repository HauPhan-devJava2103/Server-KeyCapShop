package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vn.keycap_server.modal.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

}
