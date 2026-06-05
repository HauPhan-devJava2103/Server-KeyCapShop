package com.vn.keycap_server.service.auth.login;

import com.vn.keycap_server.dto.response.LoginResponse;

public interface ILoginHandler<T> {
    LoginResponse login(T request);

    String getLoginType();

}
