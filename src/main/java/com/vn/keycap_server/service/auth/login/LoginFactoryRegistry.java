package com.vn.keycap_server.service.auth.login;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.service.auth.login.creator.AbstractLoginCreator;
import com.vn.keycap_server.service.auth.login.creator.BasicLoginCreator;
import com.vn.keycap_server.service.auth.login.creator.GoogleLoginCreator;
import com.vn.keycap_server.service.auth.login.product.BasicLoginHandler;
import com.vn.keycap_server.service.auth.login.product.GoogleLoginHandler;
import com.vn.keycap_server.utils.ELoginType;

@Component
public class LoginFactoryRegistry {
    private final BasicLoginHandler basicLoginHandler;
    private final GoogleLoginHandler googleLoginHandler;

    public LoginFactoryRegistry(BasicLoginHandler basicLoginHandler, GoogleLoginHandler googleLoginHandler) {
        this.basicLoginHandler = basicLoginHandler;
        this.googleLoginHandler = googleLoginHandler;
    }

    public AbstractLoginCreator getCreator(ELoginType loginType) {
        return switch (loginType) {
            case BASIC -> new BasicLoginCreator(basicLoginHandler);
            case GOOGLE -> new GoogleLoginCreator(googleLoginHandler);
        };
    }

}