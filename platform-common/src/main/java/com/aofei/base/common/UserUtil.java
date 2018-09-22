package com.aofei.base.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @auther Tony
 * @create 2018-05-03 17:50
 */
public class UserUtil {

    /**
     * 获取保存在Session中的用户对象
     * @param <T>
     * @return
     */
    public static  <T> T getSessionUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (T) request.getSession().getAttribute(Const.SESSION_USER);

    }
    /**
     * 获取保存在Session中的用户对象
     * @return
     */
    public static  void setSessionUser(Object o) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.getSession().setAttribute(Const.SESSION_USER,o);

    }


    /**
     * 获取保存在Session中的用户对象
     * @param <T>
     * @return
     */
    public static <T> T getSessionUser(HttpServletRequest request) {
        return (T) request.getSession().getAttribute(Const.SESSION_USER);
    }
}
