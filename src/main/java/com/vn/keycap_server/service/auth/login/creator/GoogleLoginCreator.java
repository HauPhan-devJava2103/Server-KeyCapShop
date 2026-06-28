package com.vn.keycap_server.service.auth.login.creator;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.service.auth.login.product.GoogleLoginHandler;
import com.vn.keycap_server.service.auth.login.product.ILoginHandler;
import com.vn.keycap_server.utils.ELoginType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleLoginCreator extends AbstractLoginCreator {

    private final GoogleLoginHandler googleLoginHandler;

    @Override
    public ILoginHandler<?> createHandler() {
        return googleLoginHandler;
    }

    @Override
    public ELoginType getLoginType() {
        return ELoginType.GOOGLE;
    }
}