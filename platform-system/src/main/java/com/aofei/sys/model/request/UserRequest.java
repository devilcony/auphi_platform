package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @auther Tony
 * @create 2018-09-15 15:06
 */
@Setter
@Getter
public class UserRequest<User> extends BaseRequest<User> {

    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 状态  0：正常   1：禁用
     */
    private Integer status;
    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private Date loginTime;

    public UserRequest() {

    }

    public UserRequest(Long userId) {
       setUserId(userId);
    }
}
