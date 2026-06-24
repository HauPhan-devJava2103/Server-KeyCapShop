package com.vn.keycap_server.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.service.auth.TokenService;

@Component
public class AuthTestHelper {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TestDataFactory testDataFactory;

    public String getUserToken() {
        User user = testDataFactory.createDummyUser();
        return generateValidToken(user);
    }

    public String getAdminToken() {
        User admin = testDataFactory.createDummyAdmin();
        return generateValidToken(admin);
    }

    public String generateValidToken(User user) {
        String token = tokenService.generateToken(user);
        tokenService.saveUserToken(user, token);
        return "Bearer " + token;
    }
}
