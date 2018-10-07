package com.aofei.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Hao
 * @create 2017-04-10
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
    /**
     * 逗号相隔的字符串转Integer数组
     *
     * @param str
     * @return
     */
    public static Integer[] toInts(String str) {
        return toInts(str, ",");
    }
    /**
     * 根据分隔符把字符串转为Integer数组
     *
     * @param str
     * @param separator
     * @return
     */
    public static Integer[] toInts(String str, String separator) {
        if (str != null && str.trim().length() > 0) {
            String temp = trimComma(str);
            String[] strs = org.apache.commons.lang3.StringUtils.split(temp, separator);
            if (strs != null && strs.length > 0) {
                Integer[] ids = new Integer[strs.length];
                for (int i = 0; i < strs.length; i++) {
                    String s = strs[i];
                    if (s != null && s.trim().length() > 0) {
                        ids[i] = Integer.parseInt(strs[i]);
                    }
                }
                return ids;
            }
        }
        return null;
    }

    /**
     * 去掉字符串首尾逗号
     *
     * @param str
     * @return
     */
    public static String trimComma(String str) {
        String regex = "^,*|,*$";
        return str.replaceAll(regex, "");
    }

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("X-Forwarded-For");
        }else if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        }else if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
    }

    /**
     * 从国际化资源配置文件中根据key获取value  方法一
     * @param key
     * @return
     */
    public static String getMessage(String key){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        RequestContext requestContext = new RequestContext(request);
        return requestContext.getMessage(key);
    }


    public static String getMessage(String key, String[] ags) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        RequestContext requestContext = new RequestContext(request);
        return requestContext.getMessage(key,ags);
    }

    public static String createName(String mobile) {
        if(!isEmpty(mobile)){
            char[] s = mobile.toCharArray();

            int n = s.length<3? 1: mobile.length()/3;

            for(int i = n ; i < s.length-n ; i++){
                s[i] = '*';
            }
            return new String(s);
        }else{
            int a = (int)((Math.random()*9+1)*1000) ;//4为随机数
            return "member"+a;
        }
    }

    public static String defaultString(Integer str) {
        return str == null ? "" : String.valueOf(str);
    }

}
