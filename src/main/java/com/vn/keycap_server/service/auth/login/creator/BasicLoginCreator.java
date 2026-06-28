package com.vn.keycap_server.service.auth.login.creator;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.service.auth.login.product.BasicLoginHandler;
import com.vn.keycap_server.service.auth.login.product.ILoginHandler;
import com.vn.keycap_server.utils.ELoginType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BasicLoginCreator extends AbstractLoginCreator {
    private final BasicLoginHandler basicLoginHandler;

    @Override
    public ILoginHandler<?> createHandler() {
        return basicLoginHandler;
    }

    @Override
    public ELoginType getLoginType() {
        return ELoginType.BASIC;
    }
}