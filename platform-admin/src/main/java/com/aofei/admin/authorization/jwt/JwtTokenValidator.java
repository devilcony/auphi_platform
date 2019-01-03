package com.aofei.admin.authorization.jwt;

import com.aofei.authorization.manager.TokenValidator;
import com.aofei.base.common.Const;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Hao
 * @create 2017-04-10
 */
@Component
public class JwtTokenValidator  implements TokenValidator {

    @Autowired
    private JwtTokenBuilder jwtTokenBuilder;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean validate(String token) {
        //验证token
        Claims claims = jwtTokenBuilder.decodeToken(token, jwtConfig.getBase64Secret());
        if (claims != null) {
            //如果token验证成功
            String subject = claims.getSubject();//用户信息
            return true;
        }
        return false;
    }

    @Override
    public String getKey(String token) {
        //验证token
        Claims claims = jwtTokenBuilder.decodeToken(token, jwtConfig.getBase64Secret());
        if (claims != null) {
            //如果token验证成功
            String subject = claims.getSubject();//用户信息
            Map<String,String> map = jwtTokenBuilder.decodeSubject(subject);
            return map != null ? map.get(Const.TOKEN_KEY) : null;
        }
        return null;
    }
}
