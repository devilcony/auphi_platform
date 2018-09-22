package com.aofei.base.common;

import com.aofei.utils.PropertiesLoader;
import com.aofei.utils.StringUtils;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 常量类
 * Created by Hao on 2017-03-24.
 */
public class Const {

    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = Maps.newHashMap();

    /**
     * 属性文件加载对象
     */
    private static PropertiesLoader loader = new PropertiesLoader("config/application.properties");

    /** session中存放的用户key*/
    public final static String SESSION_USER = "user";

    public static final String PUSH_TAG_USER = "USER_";

    public static final String PUSH_TAG_USER_TYPE = "USER_TYPE_";

    public static final String PUSH_TAG_CLASS = "CLASS_";

    /**未付款**/
    public static final int ORDER_STATUS_PAY_NO = 0;
    /**已付款**/
    public static final int ORDER_STATUS_PAID = 1;
    /**申请退款**/
    public static final int ORDER_STATUS_REFUND = 2;
    /**退款中**/
    public static final int ORDER_STATUS_REFUNDING = 3;
    /**已退款**/
    public static final int ORDER_STATUS_REFUNDED = 4;
    /**取消交易**/
    public static final int ORDER_STATUS_CANCELLED = 5;

    /**
     * 直播状态 0：未直播
     */
    public static final  Integer LIVE_STATUS_NO_ING = 0;

    public static final String SMS_CAPTCHA_SESSION_KEY = "SMS_CAPTCHA_SESSION_KEY";

    /**
     * 直播状态 0：直播中
     */
    public static final  Integer LIVE_STATUS_ING = 1;

    public static final  int YES = 1;

    public static final  int NO = 0;

    public static final  String USER_SIG = "userSig";

    /**
     * 获取配置
     * @see ${fns:getConfig('adminPath')}
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null){
            value = loader.getProperty(key);
            map.put(key, value != null ? value : StringUtils.EMPTY);
        }
        return value;
    }




}
