package com.aofei.admin.authorization.resolver;

import com.aofei.authorization.repository.UserModelRepository;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.sys.model.response.UserResponse;
import com.aofei.sys.service.IUserService;
import com.aofei.utils.BeanCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Hao
 * @create 2017-04-10
 */

@Component
public class UserRepository implements UserModelRepository {

    @Autowired
    private IUserService userService;

    @Override
    public Object getCurrentUser(String username) {
        UserResponse response = userService.get(username);
        return  response !=null? BeanCopier.copy(response, CurrentUserResponse.class) : null;
    }
}
