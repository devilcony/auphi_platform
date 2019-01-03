/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.aofei.authorization.resolvers;

import com.aofei.base.annotation.CurrentUser;
import com.aofei.authorization.interceptor.AuthorizationInterceptor;
import com.aofei.authorization.repository.UserModelRepository;
import com.aofei.base.common.UserUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * 增加方法注入，将含有CurrentUser注解的方法参数注入当前登录用户
 */
public class CurrentUserMethodArgumentResolver<T> implements HandlerMethodArgumentResolver {

    //用户模型的类名
    private Class<T> userModelClass;

    //通过Key获得用户模型的实现类
    private UserModelRepository<T> userModelRepository;

    public void setUserModelClass(String className) {
        try {
            this.userModelClass = (Class<T>) Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserModelClass(Class<T> clazz) {
        this.userModelClass = clazz;
    }

    public void setUserModelRepository(UserModelRepository<T> userModelRepository) {
        this.userModelRepository = userModelRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果参数类型是User并且有CurrentUser注解则支持
        return parameter.getParameterType().isAssignableFrom(userModelClass) &&
                parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //取出鉴权时存入的登录用户Id
        Object object = webRequest.getAttribute(AuthorizationInterceptor.REQUEST_CURRENT_KEY, RequestAttributes.SCOPE_REQUEST);
        if (object != null) {
            String username = object.toString();
            //从数据库中查询并返回
            Object userModel = userModelRepository.getCurrentUser(username);
            if (userModel != null) {
                UserUtil.setSessionUser(userModel);
                return userModel;
            }
            //有key但是得不到用户，抛出异常
            throw new MissingServletRequestPartException(AuthorizationInterceptor.REQUEST_CURRENT_KEY);
        }
        //没有key就直接返回null
        return null;
    }
}
