package com.aofei.utils;


import org.apache.commons.validator.routines.TimeValidator;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 实用工具类
 * Created by Hao on 2017-03-26.
 */
public class Utils {

    /**
     * 是否为空
     *
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            String instance = (String) obj;
            if (instance.trim().length() <= 0 || "null".equalsIgnoreCase(instance)) {
                return true;
            }
        } else if (obj instanceof Integer) {
            Integer instance = (Integer) obj;
            if (instance < 0) {
                return true;
            }
        } else if (obj instanceof List<?>) {
            List<?> instance = (List<?>) obj;
            if (instance.size() <= 0) {
                return true;
            }
        } else if (obj instanceof Map<?, ?>) {
            Map<?, ?> instance = (Map<?, ?>) obj;
            if (instance.size() <= 0) {
                return true;
            }
        } else if (obj instanceof Object[]) {
            Object[] instance = (Object[]) obj;
            if (instance.length <= 0) {
                return true;
            }
        } else if (obj instanceof Long) {
            Long instance = (Long) obj;
            if (instance < 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static void main(String[] args) {
        long l1 = System.currentTimeMillis();
        TimeValidator validator = TimeValidator.getInstance();// Get the Date validator

        Calendar fooDate = validator.validate("12:00:00", "HH:mm:ss");// Validate/Convert the date

        System.out.println(fooDate);
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1);




    }
}
