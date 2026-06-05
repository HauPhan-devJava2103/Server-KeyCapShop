package com.vn.keycap_server.service.auth.login;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.exception.BadRequestException;

@Component
public class LoginHandlerFactory {
    private final Map<String, ILoginHandler> handlerMap;

    public LoginHandlerFactory(List<ILoginHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        ILoginHandler::getLoginType,
                        Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <T> ILoginHandler<T> getHandler(String loginType) {
        ILoginHandler<T> handler = handlerMap.get(loginType);
        if (handler == null) {
            throw new BadRequestException("Phương thức đăng nhập '" + loginType + "' không được hỗ trợ.");
        }
        return handler;
    }

}
