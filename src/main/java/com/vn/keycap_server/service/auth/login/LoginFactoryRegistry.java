package com.vn.keycap_server.service.auth.login;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.service.auth.login.creator.AbstractLoginCreator;
import com.vn.keycap_server.utils.ELoginType;

@Component
public class LoginFactoryRegistry {

    private final Map<ELoginType, AbstractLoginCreator> creatorMap;

    public LoginFactoryRegistry(List<AbstractLoginCreator> creators) {
        this.creatorMap = creators.stream()
                .collect(Collectors.toMap(
                        AbstractLoginCreator::getLoginType,
                        Function.identity()));
    }

    public AbstractLoginCreator getCreator(ELoginType loginType) {
        AbstractLoginCreator creator = creatorMap.get(loginType);
        if (creator == null) {
            throw new BadRequestException(
                    "Phương thức đăng nhập '" + loginType + "' không được hỗ trợ.");
        }
        return creator;
    }

}
