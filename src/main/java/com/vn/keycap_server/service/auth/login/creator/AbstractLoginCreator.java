package com.vn.keycap_server.service.auth.login.creator;

import com.vn.keycap_server.dto.response.auth.LoginResponse;
import com.vn.keycap_server.service.auth.login.product.ILoginHandler;
import com.vn.keycap_server.utils.ELoginType;

public abstract class AbstractLoginCreator {

    public abstract ILoginHandler<?> createHandler();

    public abstract ELoginType getLoginType();

    @SuppressWarnings("unchecked")
    public <T> LoginResponse executeLogin(T request) {
        ILoginHandler<T> handler = (ILoginHandler<T>) createHandler();
        return handler.login(request);
    }



}
